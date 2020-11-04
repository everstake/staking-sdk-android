package com.everstake.staking.sdk

/**
 * created by Alex Ivanov on 02.11.2020.
 */
interface EverstakeListener {
    fun onAction(
        actionType: EverstakeAction,
        coinSymbol: String,
        payload: Map<String, Any>
    )
}

enum class EverstakeAction {
    STAKE, UNSTAKE, CLAIM
}