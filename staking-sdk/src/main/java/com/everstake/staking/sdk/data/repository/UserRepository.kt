package com.everstake.staking.sdk.data.repository

import com.everstake.staking.sdk.EverstakeBalanceModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * created by Alex Ivanov on 03.11.2020.
 */
internal class UserBalanceRepository private constructor() {

    companion object {
        val instance: UserBalanceRepository by lazy { UserBalanceRepository() }
    }

    private val userAddressInfo: BroadcastChannel<List<EverstakeBalanceModel>> =
        ConflatedBroadcastChannel()

    init {
        userAddressInfo.offer(emptyList())
    }

    fun updateBalance(balances: List<EverstakeBalanceModel>) {
        userAddressInfo.offer(balances
            .distinctBy { it.coinSymbol }
            .map { it.copy(coinSymbol = it.coinSymbol.toUpperCase(Locale.ENGLISH)) }
        )
    }

    fun getAddressInfoFlow(): Flow<List<EverstakeBalanceModel>> =
        userAddressInfo.asFlow().distinctUntilChanged()

    fun getSupportedCoinsFlow(): Flow<List<String>> =
        getAddressInfoFlow().map { list: List<EverstakeBalanceModel> -> list.map { it.coinSymbol } }

    fun getBalanceForCoinSymbol(coinSymbolFlow: Flow<String>): Flow<String?> =
        coinSymbolFlow.combine(getAddressInfoFlow()) { coinSymbol: String?, addressInfoList: List<EverstakeBalanceModel>? ->
            addressInfoList?.find { it.coinSymbol.equals(coinSymbol, true) }?.balance
        }
}