package com.everstake.staking.sdk.data.repository

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.data.api.ApiResult
import com.everstake.staking.sdk.data.api.callEverstakeApi
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.Validator
import com.everstake.staking.sdk.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal class CoinListRepository
private constructor(
    private val userBalanceRepository: UserBalanceRepository = UserBalanceRepository.instance
) {

    companion object {
        val instance: CoinListRepository by lazy { CoinListRepository() }
    }

    suspend fun refreshCoinList(
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ): ApiResult<List<GetCoinsResponseModel>> {
        val cachedCoinList: List<GetCoinsResponseModel>? = readCache<List<GetCoinsResponseModel>>(
            EverstakeStaking.app,
            CacheType.COIN,
            updateTimeout
        )
        return if (cachedCoinList == null) {
            val apiResult: ApiResult<List<GetCoinsResponseModel>> =
                callEverstakeApi { getSupportedCoins() }
            if (apiResult is ApiResult.Success) {
                storeCache(EverstakeStaking.app, CacheType.COIN, apiResult.result)
            }
            apiResult
        } else {
            ApiResult.Success(cachedCoinList)
        }
    }

    fun getCoinListFlowNullable(): Flow<List<GetCoinsResponseModel>?> = readCacheAsFlow(
        EverstakeStaking.app, CacheType.COIN
    ).distinctUntilChanged().map { cachedData: CacheData ->
        Gson().parseWithType<List<GetCoinsResponseModel>>(cachedData.dataJson)
    }.combine(userBalanceRepository.getSupportedCoinsFlow())
    { coinList: List<GetCoinsResponseModel>?, supportedCoins: List<String>? ->
        coinList ?: return@combine null
        supportedCoins ?: return@combine null
        coinList.filter { coin ->
            supportedCoins.any { supportedCoin -> supportedCoin.equals(coin.coinSymbol, true) }
        }
    }

    fun getCoinListFlow(): Flow<List<GetCoinsResponseModel>> =
        getCoinListFlowNullable().filterNotNull()

    fun getCoinInfoFlow(coinIdFlow: Flow<String>): Flow<GetCoinsResponseModel> =
        coinIdFlow.combine(getCoinListFlow()) { coinId: String?, coinList: List<GetCoinsResponseModel>? ->
            coinList?.find { it.id == coinId }
        }.filterNotNull()

    fun findValidatorInfoFlow(
        coinInfoFlow: Flow<GetCoinsResponseModel>,
        selectedValidatorsFlow: Flow<List<String>>
    ): Flow<List<Validator>> =
        coinInfoFlow.map { it.validators }
            .combine(selectedValidatorsFlow) { validators: List<Validator>?, selectedValidators: List<String> ->
                validators?.takeIf { it.isNotEmpty() } ?: return@combine null
                validators.filter { selectedValidators.contains(it.id) }
                    .takeIf { it.isNotEmpty() }
                    ?: validators.firstOrNull { it.isReliable }?.let { listOf(it) }
                    ?: listOf(validators.first())
            }.filterNotNull()
}