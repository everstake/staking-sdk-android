package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetValidatorsApiResponse
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.data.repository.ValidatorRepository
import com.everstake.staking.sdk.util.bindString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 25.10.2020.
 */
internal class GetValidatorsListUseCase(
    private val validatorsRepository: ValidatorRepository = ValidatorRepository.instance
) {
    fun getValidatorListFlow(
        coinId: Flow<String>,
        validatorId: Flow<String>
    ): Flow<List<ValidatorListModel>> = validatorsRepository
        .getValidatorFlow(coinId)
        .combine(validatorId) { validatorList: List<GetValidatorsApiResponse>?, selectedValidatorId: String? ->
            validatorList?.map { (id: String, name: String, fee: BigDecimal, isReliable: Boolean) ->
                ValidatorListModel(
                    id = id,
                    isSelected = id == selectedValidatorId,
                    name = name,
                    fee = bindString(EverstakeStaking.app, R.string.common_percent_format, fee),
                    isReliable = isReliable
                )
            } ?: emptyList()
        }
}