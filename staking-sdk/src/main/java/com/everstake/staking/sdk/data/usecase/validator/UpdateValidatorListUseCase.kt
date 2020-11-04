package com.everstake.staking.sdk.data.usecase.validator

import com.everstake.staking.sdk.data.repository.ValidatorRepository
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 25.10.2020.
 */
internal class UpdateValidatorListUseCase(
    private val validatorRepository: ValidatorRepository = ValidatorRepository.instance
) {
    suspend fun updateValidators(
        coinId: String,
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ) {
        validatorRepository.refreshValidatorsForCoinId(coinId, updateTimeout)
    }
}