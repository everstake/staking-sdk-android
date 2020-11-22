package com.everstake.staking.sdk.data.usecase.validator

import com.everstake.staking.sdk.data.repository.CoinListRepository
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 25.10.2020.
 */
internal class UpdateValidatorListUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance
) {
    suspend fun updateValidators(
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ) {
        coinListRepository.refreshCoinList(updateTimeout)
    }
}