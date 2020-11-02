package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import com.everstake.staking.sdk.data.model.ui.CoinDetailsModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.StakedRepository
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal class GetCoinDetailsUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val stakedRepository: StakedRepository = StakedRepository.instance
) {
    fun getCoinDetailsFlow(coinIdFlow: Flow<String>): Flow<CoinDetailsModel> {
        val coinInfoFlow: Flow<GetCoinsResponseModel> =
            coinListRepository.getCoinInfoFlow(coinIdFlow)
        val stakeInfoFlow: Flow<PutStakeResponseModel?> =
            stakedRepository.getStakeInfoFlow(coinIdFlow)
        return combine(
            coinInfoFlow,
            stakeInfoFlow
        ) { coinInfo: GetCoinsResponseModel?,
            stakedInfo: PutStakeResponseModel? ->
            coinInfo ?: return@combine null

            val apr: String = bindString(
                EverstakeStaking.app,
                R.string.common_percent_format,
                coinInfo.apr
            )
            val fee: String = coinInfo.fee.let { (min: BigDecimal, max: BigDecimal) ->
                if (min != max) "$min-$max" else min.toString()
            }.let {
                bindString(EverstakeStaking.app, R.string.common_percent_format, it)
            }

            val showStaked: Boolean =
                coinInfo.isActive && stakedInfo != null && stakedInfo.amount > BigDecimal.ZERO
            val stakedAmount: String = formatAmount(
                amount = stakedInfo?.amount ?: BigDecimal.ZERO,
                precision = coinInfo.precision,
                symbol = coinInfo.symbol
            )
            val income: String =
                bindString(EverstakeStaking.app, R.string.common_percent_format, coinInfo.apr)

            val claimAmount: BigDecimal = stakedInfo?.amountToClaim ?: BigDecimal.ZERO
            val showClaim: Boolean = showStaked && claimAmount > BigDecimal.ZERO

            CoinDetailsModel(
                id = coinInfo.id,
                displayName = "${coinInfo.name} (${coinInfo.symbol})",
                iconUrl = coinInfo.iconUrl,
                about = coinInfo.about,
                aboutUrl = coinInfo.aboutUrl,
                apr = apr,
                serviceFee = fee,
                showStakedSection = showStaked,
                stakedAmount = stakedAmount,
                validatorName = stakedInfo?.validator?.name ?: "",
                yearlyIncome = income,
                showClaimSection = showClaim,
                availableToClaim = formatAmount(claimAmount, coinInfo.precision, coinInfo.symbol)
            )
        }.filterNotNull()
    }
}