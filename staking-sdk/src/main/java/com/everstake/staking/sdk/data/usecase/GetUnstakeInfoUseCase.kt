package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.data.Constants.MAX_DISPLAY_PRECISION
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import com.everstake.staking.sdk.data.model.api.StakeType
import com.everstake.staking.sdk.data.model.api.Validator
import com.everstake.staking.sdk.data.model.ui.UnstakeModel
import com.everstake.staking.sdk.data.model.ui.UnstakeValidatorModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.StakedRepository
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal
import kotlin.math.max

/**
 * created by Alex Ivanov on 30.10.2020.
 */
internal class GetUnstakeInfoUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val stakedRepository: StakedRepository = StakedRepository.instance
) {

    private var previousProgress: BigDecimal = BigDecimal.ZERO

    fun getUnstakeFlow(
        coinIdFlow: Flow<String>,
        selectedValidatorsFlow: Flow<List<String>>,
        amountFlow: Flow<String>,
        progressFlow: Flow<BigDecimal>
    ): Flow<UnstakeModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinInfoFlow(coinIdFlow)
        val stakedInfoFlow: Flow<PutStakeResponseModel?> =
            stakedRepository.getStakeInfoFlow(coinIdFlow)

        return combine(
            coinInfoFlow,
            stakedInfoFlow,
            selectedValidatorsFlow,
            amountFlow,
            progressFlow
        )
        { coinInfo: GetCoinsResponseModel?, stakedInfo: PutStakeResponseModel?, selectedValidators: List<String>?, amountStr: String?, progressIn: BigDecimal? ->
            coinInfo ?: return@combine null
            amountStr ?: return@combine null
            progressIn ?: return@combine null

            val validators: List<Validator> =
                stakedInfo?.validators?.filter { selectedValidators?.contains(it.id) == true }
                    ?: emptyList()
            // In case 1 to N there will be multiple validators, but the will have the same amount in other cases there will be only one validator
            val totalBalance: BigDecimal =
                if (coinInfo.stakeType == StakeType.OneStakeToMultipleValidators) {
                    stakedInfo?.amount
                } else {
                    validators.firstOrNull()?.amount
                } ?: BigDecimal.ZERO

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

            UnstakeModel(
                balance = formatAmount(totalBalance, coinInfo.precision, coinInfo.coinSymbol),
                amount = if (amount == initialAmount) amountStr
                else formatAmount(amount, coinInfo.precision),
                symbol = coinInfo.coinSymbol,
                progress = progress,
                unstakeTimeSeconds = coinInfo.intervalUnstake,
                canUnstake = totalBalance >= amount,
                validators = validators.map { UnstakeValidatorModel(it.name, it.address) }
            )
        }.filterNotNull()
    }
}