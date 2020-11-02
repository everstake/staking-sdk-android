package com.everstake.staking.sdk.data.repository

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.data.api.ApiResult
import com.everstake.staking.sdk.data.api.EverstakeApi
import com.everstake.staking.sdk.data.api.callEverstakeApi
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import com.everstake.staking.sdk.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal class StakedRepository private constructor() {
    companion object {
        val instance: StakedRepository by lazy { StakedRepository() }
    }

    suspend fun refreshStaked(
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ): List<PutStakeResponseModel> {
        val cachedStakedList: List<PutStakeResponseModel>? = readCache<List<PutStakeResponseModel>>(
            EverstakeStaking.app,
            CacheType.STAKE,
            updateTimeout
        )
        return if (cachedStakedList == null) {
            val apiResult: ApiResult<List<PutStakeResponseModel>> =
                callEverstakeApi { api: EverstakeApi ->
                    // TODO provide address list
                    api.getStakedInfo(emptyList())
                }
            if (apiResult is ApiResult.Success) {
                apiResult.result.also { result: List<PutStakeResponseModel> ->
                    storeCache(EverstakeStaking.app, CacheType.STAKE, result)
                }
            } else {
                emptyList()
            }
        } else {
            cachedStakedList
        }
    }

    fun getStakedFlow(): Flow<List<PutStakeResponseModel>> = readCacheAsFlow(
        EverstakeStaking.app, CacheType.STAKE
    ).distinctUntilChanged().map { cachedData: CacheData ->
        Gson().parseWithType(cachedData.dataJson)
    }

    fun getStakeInfoFlow(coinIdFlow: Flow<String>): Flow<PutStakeResponseModel?> =
        coinIdFlow.combine(getStakedFlow()) { coinId: String?, stakedList: List<PutStakeResponseModel>? ->
            stakedList?.find { it.coinId == coinId }
        }
}