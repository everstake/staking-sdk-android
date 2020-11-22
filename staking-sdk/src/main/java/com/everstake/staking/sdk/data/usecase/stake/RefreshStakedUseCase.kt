package com.everstake.staking.sdk.data.usecase.stake

import com.everstake.staking.sdk.data.model.api.GetCoinsResponseModel
import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.StakedRepository
import com.everstake.staking.sdk.data.repository.UserBalanceRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 04.11.2020.
 */
internal class RefreshStakedUseCase(
    private val stakedRepository: StakedRepository = StakedRepository.instance,
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val userRepository: UserBalanceRepository = UserBalanceRepository.instance
) {

    suspend fun updateStaked(
        refreshInfo: Map<String, String>? = null,
        updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)
    ) {
        val coinSymbolToAddressMap: Map<String, String>? =
            (refreshInfo ?: userRepository.getAddressInfoFlow().firstOrNull()
                ?.map { it.coinSymbol to it.address }
                ?.toMap())
                ?.takeIf { it.isNotEmpty() }

        val coinList: List<GetCoinsResponseModel> =
            coinListRepository.getCoinListFlowNullable().firstOrNull() ?: return

        val coinIdToAddressMap: Map<String, String> =
            coinSymbolToAddressMap?.map { (coinSymbol: String, address: String) ->
                val coinInfo: GetCoinsResponseModel? = coinList.find { it.symbol == coinSymbol }
                if (coinInfo != null) Pair(coinInfo.id, address) else null
            }?.filterNotNull()
                ?.takeIf { it.isNotEmpty() }
                ?.toMap()
                ?: return

        stakedRepository.refreshStaked(coinIdToAddressMap, updateTimeout)
    }
}