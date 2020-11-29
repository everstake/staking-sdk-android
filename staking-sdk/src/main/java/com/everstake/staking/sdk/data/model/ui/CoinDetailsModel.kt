package com.everstake.staking.sdk.data.model.ui

import com.everstake.staking.sdk.data.model.api.StakeType

/**
 * created by Alex Ivanov on 16.10.2020.
 */
internal data class CoinDetailsModel(
    val id: String,
    val coinName: String,
    val coinSymbol: String,
    val stakeType: StakeType,
    val iconUrl: String,
    val about: String,
    val aboutUrl: String,
    val apr: String,
    val serviceFee: String,
    val showStakedSection: Boolean,
    val totalStakedAmount: String,
    val validators: List<CoinDetailsValidatorInfo>,
    val showClaimSection: Boolean,
    val claimAmount: String
)

internal data class CoinDetailsValidatorInfo(
    val validatorId: String,
    val validatorName: String,
    val validatorAddress: String,
    val stakedAmount: String,
)