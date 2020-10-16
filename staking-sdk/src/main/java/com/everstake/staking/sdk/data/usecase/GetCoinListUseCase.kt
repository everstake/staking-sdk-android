package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.api.PutStakeResponseModel
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.data.model.ui.SectionData
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.StakedRepository
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.formatAmount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal

/**
 * created by Alex Ivanov on 10.10.2020.
 */
internal class GetCoinListUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val stakedRepository: StakedRepository = StakedRepository.instance
) {

    fun getCoinListUIFlow(): Flow<List<SectionData<CoinListModel>>> =
        coinListRepository.getCoinListFlow().combine(stakedRepository.getStakedFlow())
        { coinList: List<GetCoinsResponseModel>?, stakedList: List<PutStakeResponseModel>? ->
            (coinList ?: emptyList()).sortedBy {
                it.order
            }.map { apiModel: GetCoinsResponseModel ->
                val apr: String = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    apiModel.apr
                )
                val stakedAmount: String? =
                    stakedList?.find { it.coinId == apiModel.id }
                        ?.takeIf { it.amount > BigDecimal.ZERO }
                        ?.let { formatAmount(it.amount, apiModel.precision, apiModel.symbol) }

                CoinListModel(
                    id = apiModel.id,
                    name = apiModel.name,
                    iconUrl = apiModel.iconUrl,
                    apr = apr,
                    isActive = apiModel.isActive,
                    stakedAmount = stakedAmount
                )
            }
        }.map { coinList: List<CoinListModel> ->
            coinList.groupBy { it.stakedAmount != null }
        }.map { mappedCoins: Map<Boolean, List<CoinListModel>> ->
            val staked: List<CoinListModel> = mappedCoins[true] ?: emptyList()
            val readyToStake: List<CoinListModel> = mappedCoins[false] ?: emptyList()

            val stakedSection: SectionData<CoinListModel>? =
                staked.takeIf { it.isNotEmpty() }?.let {
                    SectionData(R.string.coin_list_staked, it)
                }
            val readyToStakeSection: SectionData<CoinListModel>? =
                readyToStake.takeIf { it.isNotEmpty() }?.let {
                    SectionData(R.string.coin_list_ready_to_stake, it)
                }

            listOfNotNull(stakedSection, readyToStakeSection)
        }
}