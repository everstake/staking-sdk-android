package com.everstake.staking.sdk.data.repository

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.data.api.ApiResult
import com.everstake.staking.sdk.data.api.callEverstakeApi
import com.everstake.staking.sdk.data.model.api.GetValidatorsApiResponse
import com.everstake.staking.sdk.util.*
import com.everstake.staking.sdk.util.CacheData
import com.everstake.staking.sdk.util.CacheType
import com.everstake.staking.sdk.util.readCache
import com.everstake.staking.sdk.util.storeCache
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class ValidatorRepository private constructor() {

    companion object {
        val instance: ValidatorRepository by lazy { ValidatorRepository() }
    }

    private val gson: Gson by lazy { Gson() }

    suspend fun refreshValidatorsForCoinId(
        coinId: String,
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ): List<GetValidatorsApiResponse> {
        val cacheFile: CacheData? = readCacheFile(EverstakeStaking.app, CacheType.VALIDATORS)
        val cachedValidators: Map<String, CacheData> = cacheFile?.dataJson?.let { json: String ->
            gson.parseWithType(json)
        } ?: mapOf()
        val cachedCoinValidators: List<GetValidatorsApiResponse>? = cachedValidators[coinId]
            ?.takeIf { System.currentTimeMillis() - it.serializationTimestamp < updateTimeout }
            ?.let { (json: String) -> gson.parseWithType<List<GetValidatorsApiResponse>>(json) }

        return if (cachedCoinValidators == null) {
            val apiResult: ApiResult<List<GetValidatorsApiResponse>> = callEverstakeApi { api ->
                api.getValidatorInfo(coinId)
            }
            if (apiResult is ApiResult.Success) {
                val validators: List<GetValidatorsApiResponse> = apiResult.result
                val cachedData = CacheData(gson.toJson(validators))
                storeCache(
                    EverstakeStaking.app,
                    CacheType.VALIDATORS,
                    cachedValidators.toMutableMap().apply { put(coinId, cachedData) }
                )
                validators
            } else {
                emptyList()
            }
        } else {
            cachedCoinValidators
        }
    }

    fun getValidatorFlow(coinIdFlow: Flow<String>): Flow<List<GetValidatorsApiResponse>> =
        readCacheAsFlow(
            EverstakeStaking.app, CacheType.VALIDATORS
        ).map { cacheData ->
            gson.parseWithType<Map<String, CacheData>>(cacheData.dataJson)
        }.combine(coinIdFlow) { cache: Map<String, CacheData>?, coinId: String? ->
            coinId?.let { cache?.get(it) }?.dataJson
        }.distinctUntilChanged().map { json: String? ->
            json?.let { gson.parseWithType(json) } ?: emptyList()
        }
}