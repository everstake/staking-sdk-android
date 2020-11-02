package com.everstake.staking.sdk.data.repository

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.data.api.ApiResult
import com.everstake.staking.sdk.data.api.EverstakeApi
import com.everstake.staking.sdk.data.api.callEverstakeApi
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal class CoinListRepository private constructor() {

    companion object {
        val instance: CoinListRepository by lazy { CoinListRepository() }
    }

    suspend fun refreshCoinList(
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ): List<GetCoinsResponseModel> {
        val cachedCoinList: List<GetCoinsResponseModel>? = readCache<List<GetCoinsResponseModel>>(
            EverstakeStaking.app,
            CacheType.COIN,
            updateTimeout
        )
        return if (cachedCoinList == null) {
            val apiResult: ApiResult<List<GetCoinsResponseModel>> =
                callEverstakeApi { api: EverstakeApi ->
                    api.getSupportedCoins()
                }
            if (apiResult is ApiResult.Success) {
                apiResult.result.also { result: List<GetCoinsResponseModel> ->
                    storeCache(EverstakeStaking.app, CacheType.COIN, result)
                }
            } else {
                emptyList()
            }
        } else {
            cachedCoinList
        }
    }

    fun getCoinListFlow(): Flow<List<GetCoinsResponseModel>> = readCacheAsFlow(
        EverstakeStaking.app, CacheType.COIN
    ).distinctUntilChanged().map { cachedData: CacheData ->
        Gson().parseWithType(cachedData.dataJson)
    }

    fun getCoinInfoFlow(coinIdFlow: Flow<String>): Flow<GetCoinsResponseModel> =
        coinIdFlow.combine(getCoinListFlow()) { coinId: String?, coinList: List<GetCoinsResponseModel>? ->
            coinList?.find { it.id == coinId }
        }.filterNotNull()
}