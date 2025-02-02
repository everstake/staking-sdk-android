package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.Constants
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.StakeType
import com.everstake.staking.sdk.data.model.api.Validator
import com.everstake.staking.sdk.data.model.ui.CalculatorModel
import com.everstake.staking.sdk.data.model.ui.CalculatorValidatorInfo
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.util.CalculatorHelper
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal
import java.math.BigInteger

/**
 * created by Alex Ivanov on 26.10.2020.
 */
internal class CalculatorUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
) {
    fun getCalculatorDataFlow(
        coinIdFlow: Flow<String>,
        selectedValidatorsFlow: Flow<List<String>>,
        amountFlow: Flow<String>,
        includeValidatorFeeFlow: Flow<Boolean>,
        addReinvestmentFlow: Flow<Boolean>
    ): Flow<CalculatorModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinInfoFlow(coinIdFlow)
        val validatorsInfoFlow: Flow<List<Validator>> =
            coinListRepository.findValidatorInfoFlow(coinInfoFlow, selectedValidatorsFlow)

        return combine(
            coinInfoFlow,
            validatorsInfoFlow,
            amountFlow,
            includeValidatorFeeFlow,
            addReinvestmentFlow
        ) { coinInfo: GetCoinsResponseModel?, validators: List<Validator>?, amountStr: String?, includeValidatorFee: Boolean?, includeReinvestment: Boolean? ->
            coinInfo ?: return@combine null
            validators ?: return@combine null

            val amount: BigDecimal = amountStr?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            /* Fee and percent are received as raw percent values ie. 10% = 10, we need to convert
            * it to number representation of percent */
            val periodScale: BigDecimal = if (includeValidatorFee == true) {
                val fee: BigDecimal = validators.fold(BigDecimal.ZERO) { acc, validator ->
                    acc + validator.fee.scaleByPowerOfTen(-2)
                }
                coinInfo.yieldPercent * (BigDecimal.ONE - fee)
            } else {
                coinInfo.yieldPercent
            }.scaleByPowerOfTen(-2)

            val periodSeconds: BigInteger = coinInfo.yieldInterval
            val calcHelper = CalculatorHelper(amount, periodScale, includeReinvestment ?: false)

            val perYear: BigDecimal = calcHelper.calculate(
                Constants.YEAR_IN_SECONDS,
                periodSeconds
            )
            val perMonth: BigDecimal = calcHelper.calculate(
                Constants.MONTH_IN_SECONDS,
                periodSeconds
            )
            val perDay: BigDecimal = calcHelper.calculate(
                Constants.DAY_IN_SECONDS,
                periodSeconds
            )

            CalculatorModel(
                coinId = coinInfo.id,
                coinName = coinInfo.name,
                coinSymbol = coinInfo.coinSymbol,
                coinYearlyIncomePercent = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    coinInfo.apr
                ),
                allowMultipleValidator = coinInfo.stakeType == StakeType.OneStakeToMultipleValidators,
                validators = validators.map {
                    CalculatorValidatorInfo(
                        validatorId = it.id,
                        validatorName = it.name,
                        validatorFee = bindString(
                            EverstakeStaking.app,
                            R.string.common_percent_format,
                            it.fee
                        )
                    )
                },
                isReliableValidator = validators.any { it.isReliable },
                dailyIncome = formatAmount(perDay, coinInfo.precision, coinInfo.coinSymbol),
                monthlyIncome = formatAmount(perMonth, coinInfo.precision, coinInfo.coinSymbol),
                yearlyIncome = formatAmount(perYear, coinInfo.precision, coinInfo.coinSymbol)
            )
        }.filterNotNull()
    }
}