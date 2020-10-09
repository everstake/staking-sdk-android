package com.everstake.staking.sdk.data.model.api

import java.math.BigDecimal
import java.math.BigInteger

/**
 * created by Alex Ivanov on 09.10.2020.
 */
data class GetCoinsResponseModel(
    val id: String,
    val name: String,
    val iconUrl: String,
    val apr: BigDecimal,
    val order: Int,
    val yieldInterval: BigInteger,
    val yieldPercent: BigDecimal,
    val isActive: Boolean,
    val symbol: String,
    val precision: Int,
    val needsClaiming: Boolean,
    val intervalStake: BigInteger,
    val intervalUnstake: BigInteger,
    val toUsd: BigDecimal
)