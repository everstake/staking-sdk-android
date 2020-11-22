package com.everstake.staking.sdk.data.usecase.stake

import com.everstake.staking.sdk.data.usecase.coins.UpdateCoinListUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal class UpdateCoinDetailsUseCase(
    private val updateCoinListUseCase: UpdateCoinListUseCase = UpdateCoinListUseCase(),
    private val updateStakedUseCase: RefreshStakedUseCase = RefreshStakedUseCase()
) {

    suspend fun updateData(updateTimeout: Long = TimeUnit.MINUTES.toMillis(10)): Boolean =
        coroutineScope {
            val updateCoinList = async {
                updateCoinListUseCase.updateCoins(updateTimeout)
            }
            val updateStaked = async {
                updateStakedUseCase.updateStaked(updateTimeout = updateTimeout)
            }
            updateStaked.await()
            return@coroutineScope updateCoinList.await()
        }

}