package com.everstake.staking.sdk.data.usecase

import android.text.format.DateUtils
import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.GetValidatorsApiResponse
import com.everstake.staking.sdk.data.model.ui.CalculatorModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.ValidatorRepository
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 26.10.2020.
 */
internal class CalculatorUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val validatorRepository: ValidatorRepository = ValidatorRepository.instance
) {

    companion object {
        private const val INTERVAL_SCALE: Int = 5
    }

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
            val periodPercent: BigDecimal = if (includeValidatorFee == true) {
                coinInfo.yieldPercent * (BigDecimal.ONE - validatorInfo.fee.scaleByPowerOfTen(-2))
            } else {
                coinInfo.yieldPercent
            }
            // Minutes to Millis
            val periodMillis: BigDecimal =
                coinInfo.yieldInterval.toBigDecimal().scaleByPowerOfTen(3)
            val yearMs: BigDecimal =
                DateUtils.YEAR_IN_MILLIS.toBigDecimal().setScale(INTERVAL_SCALE)
            val monthMs: BigDecimal =
                DateUtils.YEAR_IN_MILLIS.toBigDecimal().setScale(INTERVAL_SCALE)
                    .divide(BigDecimal(12))
            val dayMs: BigDecimal =
                DateUtils.DAY_IN_MILLIS.toBigDecimal().setScale(INTERVAL_SCALE)

            val perYear: BigDecimal = calculateForPeriod(
                amount = amount,
                periodPercent = periodPercent,
                periodCount = yearMs / periodMillis,
                includeReinvestment = includeReinvestment ?: false
            )
            val perMonth: BigDecimal = calculateForPeriod(
                amount = amount,
                periodPercent = periodPercent,
                periodCount = monthMs / periodMillis,
                includeReinvestment = includeReinvestment ?: false
            )
            val perDay: BigDecimal = calculateForPeriod(
                amount = amount,
                periodPercent = periodPercent,
                periodCount = dayMs / periodMillis,
                includeReinvestment = includeReinvestment ?: false
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

    private fun calculateForPeriod(
        amount: BigDecimal,
        periodPercent: BigDecimal,
        periodCount: BigDecimal,
        includeReinvestment: Boolean
    ): BigDecimal {
        var count: BigDecimal = periodCount
        var income: BigDecimal = BigDecimal.ZERO
        while (count > BigDecimal.ZERO) {
            val periodSize: BigDecimal = count.min(BigDecimal.ONE)
            val periodAmount: BigDecimal = if (includeReinvestment) amount + income else amount
            income += periodAmount * periodPercent.scaleByPowerOfTen(-2) * periodSize
            count -= periodSize
        }
        return income
    }
}