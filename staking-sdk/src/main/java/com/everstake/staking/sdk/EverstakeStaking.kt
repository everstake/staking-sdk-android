package com.everstake.staking.sdk

import android.content.Context
import com.everstake.staking.sdk.ui.coin.list.CoinListActivity

/**
 * created by Alex Ivanov on 07.10.2020.
 */
object EverstakeStaking {

    fun launchStaking(context: Context) {
        context.startActivity(CoinListActivity.getIntent(context))
    }
}