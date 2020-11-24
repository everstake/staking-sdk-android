package com.everstake.staking.sdk.data.usecase.validator

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.Validator
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.util.bindString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 25.10.2020.
 */
internal class GetValidatorsListUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance
) {
    fun getValidatorListFlow(
        coinIdFlow: Flow<String>,
        validatorId: Flow<List<String>>
    ): Flow<List<ValidatorListModel>> = coinListRepository
        .getCoinInfoFlow(coinIdFlow)
        .map { it.validators }
        .combine(validatorId) { validatorList: List<Validator>?, selectedValidatorIdArray: List<String>? ->
            validatorList?.map { (id: String, name: String, fee: BigDecimal, isReliable: Boolean) ->
                ValidatorListModel(
                    id = id,
                    isSelected = selectedValidatorIdArray?.contains(id) ?: false,
                    name = name,
                    fee = bindString(EverstakeStaking.app, R.string.common_percent_format, fee),
                    isReliable = isReliable
                )
            } ?: emptyList()
        }
}