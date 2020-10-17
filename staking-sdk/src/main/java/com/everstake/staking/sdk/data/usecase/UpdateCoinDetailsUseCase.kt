package com.everstake.staking.sdk.data.usecase

import com.everstake.staking.sdk.data.repository.CoinListRepository
import com.everstake.staking.sdk.data.repository.StakedRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal class UpdateCoinDetailsUseCase(
    private val coinListRepository: CoinListRepository = CoinListRepository.instance,
    private val stakedRepository: StakedRepository = StakedRepository.instance
) {

    suspend fun updateData(updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)): Unit =
        coroutineScope {
            val updateCoinList = async {
                coinListRepository.refreshCoinList(updateTimeout)
            }
            val updateStaked = async {
                stakedRepository.refreshStaked(updateTimeout)
            }
            updateCoinList.await() + updateStaked.await()
        }

}