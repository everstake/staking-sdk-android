package com.everstake.staking.sdk.data.usecase.coins

import com.everstake.staking.sdk.data.repository.CoinListRepository
import kotlinx.coroutines.flow.first

/**
 * created by Alex Ivanov on 03.11.2020.
 */
internal class GetSupportedCoinsUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance
) {

    suspend fun getSupportedCoins(): List<String> =
        coinListRepository.getCoinListFlow().first().map { it.symbol }
}