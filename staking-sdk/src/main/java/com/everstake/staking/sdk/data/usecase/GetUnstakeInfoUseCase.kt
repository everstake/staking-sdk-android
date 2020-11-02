package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import com.everstake.staking.sdk.data.model.ui.UnstakeModel
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
        amountFlow: Flow<String>,
        progressFlow: Flow<BigDecimal>
    ): Flow<UnstakeModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinIdFlow.combine(coinListRepository.getCoinListFlow()) { coinId: String?, coinList: List<GetCoinsResponseModel>? ->
                coinList?.find { it.id == coinId }
            }.filterNotNull()
        val stakedInfoFlow: Flow<PutStakeResponseModel?> =
            coinIdFlow.combine(stakedRepository.getStakedFlow()) { coinId: String?, stakedList: List<PutStakeResponseModel>? ->
                stakedList?.find { it.coinId == coinId }
            }

        return combine(coinInfoFlow, stakedInfoFlow, amountFlow, progressFlow)
        { coinInfo: GetCoinsResponseModel?, stakedInfo: PutStakeResponseModel?, amountStr: String?, progressIn: BigDecimal? ->
            coinInfo ?: return@combine null
            amountStr ?: return@combine null
            progressIn ?: return@combine null
            val totalBalance: BigDecimal = stakedInfo?.amount ?: BigDecimal.ZERO
            val initialAmount: BigDecimal = amountStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
            var amount: BigDecimal = initialAmount
            var progress: BigDecimal = progressIn
            if (totalBalance > BigDecimal.ZERO) {
                if (previousProgress != progress) {
                    // Progress changed, recalculate Amount
                    amount = totalBalance * progress
                } else {
                    // Amount or TotalBalance changed, recalculate progress
                    progress = amount.setScale(max(5, amount.scale())) / totalBalance
                }
            } else {
                amount = BigDecimal.ZERO
            }
            this.previousProgress = progressIn

            UnstakeModel(
                balance = formatAmount(totalBalance, coinInfo.precision, coinInfo.symbol),
                amount = if (amount == initialAmount) amountStr
                else formatAmount(amount, coinInfo.precision),
                symbol = coinInfo.symbol,
                progress = progress,
                unstakeTimeSeconds = coinInfo.intervalUnstake,
                canUnstake = totalBalance >= amount
            )
        }.filterNotNull()
    }
}