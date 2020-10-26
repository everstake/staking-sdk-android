package com.everstake.staking.sdk.data.usecase

import android.text.format.DateUtils
import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.GetValidatorsApiResponse
import com.everstake.staking.sdk.data.model.ui.CalculatorModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.ValidatorRepository
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
    private val validatorRepository: ValidatorRepository = ValidatorRepository.instance
) {
    fun getCalculatorDataFlow(
        coinIdFlow: Flow<String>,
        validatorIdFlow: Flow<String?>,
        amountFlow: Flow<String>,
        includeValidatorFeeFlow: Flow<Boolean>,
        addReinvestmentFlow: Flow<Boolean>
    ): Flow<CalculatorModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinListFlow()
                .combine(coinIdFlow) { coins: List<GetCoinsResponseModel>?, coinId: String? ->
                    coins?.find { it.id == coinId }
                }.filterNotNull()
        val validatorInfoFlow: Flow<GetValidatorsApiResponse> =
            validatorRepository.getValidatorFlow(coinIdFlow)
                .combine(validatorIdFlow) { validators: List<GetValidatorsApiResponse>?, selectedValidatorId: String? ->
                    validators?.takeIf { it.isNotEmpty() } ?: return@combine null
                    validators.find { it.id == selectedValidatorId }
                        ?: validators.firstOrNull() { it.isReliable }
                        ?: validators.first()
                }.filterNotNull()

        return combine(
            coinInfoFlow,
            validatorInfoFlow,
            amountFlow,
            includeValidatorFeeFlow,
            addReinvestmentFlow
        ) { coinInfo: GetCoinsResponseModel?, validatorInfo: GetValidatorsApiResponse?, amountStr: String?, includeValidatorFee: Boolean?, includeReinvestment: Boolean? ->
            coinInfo ?: return@combine null
            validatorInfo ?: return@combine null

            val amount: BigDecimal = amountStr?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val periodScale: BigDecimal = if (includeValidatorFee == true) {
                coinInfo.yieldPercent * (BigDecimal.ONE - validatorInfo.fee.scaleByPowerOfTen(-2))
            } else {
                coinInfo.yieldPercent
            }.scaleByPowerOfTen(-2)
            // Minutes to Millis
            val periodMillis: BigInteger = coinInfo.yieldInterval.multiply(BigInteger.TEN.pow(3))
            val calcHelper = CalculatorHelper(amount, periodScale, includeReinvestment ?: false)

            val perYear: BigDecimal = calcHelper.calculate(
                DateUtils.YEAR_IN_MILLIS.toBigInteger(),
                periodMillis
            )
            val perMonth: BigDecimal = calcHelper.calculate(
                (DateUtils.YEAR_IN_MILLIS / 12).toBigInteger(),
                periodMillis
            )
            val perDay: BigDecimal = calcHelper.calculate(
                DateUtils.DAY_IN_MILLIS.toBigInteger(),
                periodMillis
            )

            CalculatorModel(
                coinId = coinInfo.id,
                coinName = coinInfo.name,
                coinSymbol = coinInfo.symbol,
                coinYearlyIncomePercent = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    coinInfo.apr
                ),
                validatorId = validatorInfo.id,
                validatorName = validatorInfo.name,
                validatorFee = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    validatorInfo.fee
                ),
                isReliableValidator = validatorInfo.isReliable,
                dailyIncome = formatAmount(perDay, coinInfo.precision, coinInfo.symbol),
                monthlyIncome = formatAmount(perMonth, coinInfo.precision, coinInfo.symbol),
                yearlyIncome = formatAmount(perYear, coinInfo.precision, coinInfo.symbol)
            )
        }.filterNotNull()
    }
}