package com.everstake.staking.sdk

import android.app.Application
import android.content.Context
import com.everstake.staking.sdk.data.usecase.coins.GetSupportedCoinsUseCase
import com.everstake.staking.sdk.data.usecase.coins.UpdateCoinListUseCase
import com.everstake.staking.sdk.data.usecase.stake.RefreshStakedUseCase
import com.everstake.staking.sdk.data.usecase.user.UpdateUserBalanceUseCase
import com.everstake.staking.sdk.ui.coin.list.CoinListActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * created by Alex Ivanov on 07.10.2020.
 */
object EverstakeStaking {

    internal lateinit var app: Application
    internal var appCallback: WeakReference<EverstakeListener> = WeakReference(null)

    /**
     * WarmUp SDK for future use. Syncs coinList, if required, for future use
     */
    fun init(application: Application) {
        this.app = application
        GlobalScope.launch {
            UpdateCoinListUseCase().updateCoins(TimeUnit.DAYS.toMillis(1))
        }
    }

    /**
     * Open staking flow
     */
    fun launchStaking(context: Context, listener: EverstakeListener) {
        appCallback = WeakReference(listener)
        context.startActivity(CoinListActivity.getIntent(context))
    }

    /**
     * Returns list of available coins for staking
     * Note: It reads file, better to move to async processing
     */
    fun getAvailableCoins(): List<String> = runBlocking {
        GetSupportedCoinsUseCase().getSupportedCoins()
    }

    /**
     * Refreshes Staked amount for provided addresses
     * @param addresses optional currency symbol to address map which will be used for refresh, if it's not provided previously set addresses will be used
     */
    @JvmOverloads
    fun refreshStaked(addresses: Map<String, String>? = null) {
        GlobalScope.launch {
            RefreshStakedUseCase().updateStaked(addresses, 0L)
        }
    }

    /**
     * Refreshes balance for user's addresses for display and stake amount
     */
    fun updateBalances(balances: List<EverstakeBalanceModel>) {
        UpdateUserBalanceUseCase().updateUserInfo(balances)
    }
}