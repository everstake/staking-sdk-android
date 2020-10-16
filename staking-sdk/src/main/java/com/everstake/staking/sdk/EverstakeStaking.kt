package com.everstake.staking.sdk

import android.app.Application
import android.content.Context
import com.everstake.staking.sdk.ui.coin.list.CoinListActivity

/**
 * created by Alex Ivanov on 07.10.2020.
 */
object EverstakeStaking {

    internal lateinit var app: Application

    fun init(application: Application) {
        this.app = application
    }

    fun launchStaking(context: Context) {
        context.startActivity(CoinListActivity.getIntent(context))
    }
}