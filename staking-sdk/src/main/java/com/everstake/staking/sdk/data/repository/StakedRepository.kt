package com.everstake.staking.sdk.data.repository

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal class StakedRepository private constructor() {
    companion object {
        val instance: StakedRepository by lazy { StakedRepository() }
    }


}