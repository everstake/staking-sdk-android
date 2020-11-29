package com.everstake.staking.sdk.data.usecase.coins

import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.model.ui.CoinSelectModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.util.bindString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class GetCoinListForSelectUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance
) {
    fun getCoinListUIFlow(): Flow<List<CoinSelectModel>> =
        coinListRepository.getCoinListFlow().map { coinList: List<GetCoinsResponseModel> ->
            coinList.filter {
                it.isActive
            }.sortedBy {
                it.order
            }.map { apiModel: GetCoinsResponseModel ->
                val apr: String = bindString(
                    EverstakeStaking.app,
                    R.string.common_percent_format,
                    apiModel.apr
                )

                CoinSelectModel(
                    id = apiModel.id,
                    name = apiModel.name,
                    iconUrl = apiModel.iconUrl,
                    apr = apr
                )
            }
        }
}