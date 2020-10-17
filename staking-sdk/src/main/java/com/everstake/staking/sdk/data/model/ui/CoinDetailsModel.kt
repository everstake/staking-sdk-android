package com.everstake.staking.sdk.data.model.ui

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal data class CoinDetailsModel(
    val id: String,
    val displayName: String,
    val iconUrl: String,
    val about: String,
    val aboutUrl: String,
    val apr: String,
    val serviceFee: String,
    val showStakedSection: Boolean,
    val stakedAmount: String,
    val validatorName: String,
    val yearlyIncome: String,
    val showClaimSection: Boolean,
    val availableToClaim: String
)