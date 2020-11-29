package com.everstake.staking.sdk

/**
 * created by Alex Ivanov on 02.11.2020.
 */
interface EverstakeListener {
    fun onAction(
        actionType: EverstakeAction,
        coinSymbol: String,
        amount: String,
        validatorsInfo: List<ValidatorInfo>
    )
}

data class ValidatorInfo(val validatorName: String, val validatorAddress: String)

enum class EverstakeAction {
    STAKE, UNSTAKE, CLAIM
}