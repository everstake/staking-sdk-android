package com.everstake.staking.sdk.data.model.ui

import java.math.BigDecimal
import java.math.BigInteger

/**
 * created by Alex Ivanov on 30.10.2020.
 */
internal data class UnstakeModel(
    val balance: String,
    val amount: String,
    val symbol: String,
    // [0,1]
    val progress: BigDecimal,
    val unstakeTimeSeconds: BigInteger,
    val canUnstake: Boolean,
    val validatorName: String,
    val validatorAddress: String
)