package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.Constants
import com.everstake.staking.sdk.data.Constants.MAX_DISPLAY_PRECISION
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.GetValidatorsApiResponse
import com.everstake.staking.sdk.data.model.ui.StakeModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.ValidatorRepository
import com.everstake.staking.sdk.util.CalculatorHelper
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.max

/**
 * created by Alex Ivanov on 02.11.2020.
 */
internal class GetStakeInfoUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val validatorRepository: ValidatorRepository = ValidatorRepository.instance
) {

    private var previousProgress: BigDecimal = BigDecimal.ZERO

    fun getStakeFlow(
        coinIdFlow: Flow<String>,
        validatorIdFlow: Flow<String?>,
        amountFlow: Flow<String>,
        progressFlow: Flow<BigDecimal>
    ): Flow<StakeModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinInfoFlow(coinIdFlow)
        val balanceFlow: Flow<String?> = flowOf("10000"/* TODO add balance to SDK */)
        val validatorInfoFlow: Flow<GetValidatorsApiResponse> =
            validatorRepository.findValidatorInfo(coinIdFlow, validatorIdFlow)

        return combine(coinInfoFlow, balanceFlow, amountFlow, progressFlow, validatorInfoFlow)
        { coinInfo: GetCoinsResponseModel?, balance: String?, amountStr: String?, progressIn: BigDecimal?, validatorInfo: GetValidatorsApiResponse? ->
            coinInfo ?: return@combine null
            amountStr ?: return@combine null
            progressIn ?: return@combine null
            validatorInfo ?: return@combine null

            val totalBalance: BigDecimal = balance?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val initialAmount: BigDecimal = amountStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
            var amount: BigDecimal = initialAmount
            var progress: BigDecimal = progressIn
            if (totalBalance > BigDecimal.ZERO) {
                if (previousProgress != progress) {
                    // Progress changed, recalculate Amount
                    amount = totalBalance * progress
                } else {
                    // Amount or TotalBalance changed, recalculate progress
                    progress =
                        amount.setScale(max(MAX_DISPLAY_PRECISION, amount.scale())) / totalBalance
                }
            } else {
                amount = BigDecimal.ZERO
            }
            this.previousProgress = progressIn

            val periodSeconds: BigInteger = coinInfo.yieldInterval
            val calcHelper =
                CalculatorHelper(amount, coinInfo.yieldPercent.scaleByPowerOfTen(-2), false)

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

            StakeModel(
                coinId = coinInfo.id,
                balance = formatAmount(totalBalance, coinInfo.precision, coinInfo.symbol),
                amount = if (amount == initialAmount) amountStr
                else formatAmount(amount, coinInfo.precision),
                progress = progress,
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