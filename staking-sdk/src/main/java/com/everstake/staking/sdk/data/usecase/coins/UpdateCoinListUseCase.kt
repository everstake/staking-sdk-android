package com.everstake.staking.sdk.data.usecase.coins

import com.everstake.staking.sdk.data.api.ApiResult
import com.everstake.staking.sdk.data.repository.CoinListRepository
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 02.11.2020.
 */
internal class UpdateCoinListUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance
) {

    suspend fun updateCoins(updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)): Boolean =
        coinListRepository.refreshCoinList(updateTimeout) is ApiResult.Success
}