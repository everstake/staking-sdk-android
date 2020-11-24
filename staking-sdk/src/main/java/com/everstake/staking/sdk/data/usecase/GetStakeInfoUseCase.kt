package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.Constants
import com.everstake.staking.sdk.data.Constants.MAX_DISPLAY_PRECISION
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.StakeType
import com.everstake.staking.sdk.data.model.api.Validator
import com.everstake.staking.sdk.data.model.ui.StakeModel
import com.everstake.staking.sdk.data.model.ui.StakeValidatorInfo
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.UserBalanceRepository
import com.everstake.staking.sdk.util.CalculatorHelper
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.max

/**
 * created by Alex Ivanov on 02.11.2020.
 */
internal class GetStakeInfoUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val userBalanceRepository: UserBalanceRepository = UserBalanceRepository.instance
) {
    private var previousProgress: BigDecimal = BigDecimal.ZERO

    fun getStakeFlow(
        coinIdFlow: Flow<String>,
        validatorsFlow: Flow<List<String>>,
        amountFlow: Flow<String>,
        progressFlow: Flow<BigDecimal>
    ): Flow<StakeModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinInfoFlow(coinIdFlow)
        val balanceFlow: Flow<String?> = userBalanceRepository.getBalanceForCoinSymbol(
            coinInfoFlow.map { it.coinSymbol }
        ).onStart { emit(null) }
        val validatorsInfoFlow: Flow<List<Validator>> =
            coinListRepository.findValidatorInfoFlow(
                coinInfoFlow,
                validatorsFlow
            )

        return combine(coinInfoFlow, balanceFlow, amountFlow, progressFlow, validatorsInfoFlow)
        { coinInfo: GetCoinsResponseModel?, balance: String?, amountStr: String?, progressIn: BigDecimal?, validatorsInfo: List<Validator>? ->
            coinInfo ?: return@combine null
            amountStr ?: return@combine null
            progressIn ?: return@combine null
            validatorsInfo ?: return@combine null

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
                balance = formatAmount(totalBalance, coinInfo.precision, coinInfo.coinSymbol),
                amount = if (amount == initialAmount) amountStr
                else formatAmount(amount, coinInfo.precision),
                progress = progress,
                coinSymbol = coinInfo.coinSymbol,
                coinYearlyIncomePercent = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    coinInfo.apr
                ),
                allowMultipleValidator = coinInfo.stakeType == StakeType.OneStakeToMultipleValidators,
                validators = validatorsInfo.map {
                    StakeValidatorInfo(
                        validatorId = it.id,
                        validatorName = it.name,
                        validatorFee = bindString(
                            EverstakeStaking.app,
                            R.string.common_percent_format,
                            it.fee
                        ),
                        validatorAddress = it.address
                    )
                },
                isReliableValidator = validatorsInfo.any { it.isReliable },
                dailyIncome = formatAmount(perDay, coinInfo.precision, coinInfo.coinSymbol),
                monthlyIncome = formatAmount(perMonth, coinInfo.precision, coinInfo.coinSymbol),
                yearlyIncome = formatAmount(perYear, coinInfo.precision, coinInfo.coinSymbol)
            )
        }.filterNotNull()
    }
}