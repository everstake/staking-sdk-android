package com.everstake.staking.sample

import android.app.Application
import com.everstake.staking.sdk.EverstakeStaking

/**
 * created by Alex Ivanov on 08.10.2020.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EverstakeStaking.init(this)
    }
}