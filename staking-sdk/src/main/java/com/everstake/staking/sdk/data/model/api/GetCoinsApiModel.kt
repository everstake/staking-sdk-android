package com.everstake.staking.sdk.data.model.api

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * created by Alex Ivanov on 09.10.2020.
 */
internal data class GetCoinsResponseModel(
    val id: String,
    val name: String,
    val stakeType: StakeType,
    val iconUrl: String,
    val apr: BigDecimal,
    val order: Int,
    val yieldInterval: BigInteger,
    val yieldPercent: BigDecimal,
    val isActive: Boolean,
    private val symbol: String,
    val precision: Int,
    val needsClaiming: Boolean,
    val intervalStake: BigInteger,
    val intervalUnstake: BigInteger,
    val toUsd: BigDecimal,
    val about: String,
    val aboutUrl: String,
    val validators: List<Validator>
) {
    val coinSymbol: String
        get() = symbol.toUpperCase(Locale.ENGLISH)
}

internal data class Validator(
    val id: String,
    val name: String,
    val fee: BigDecimal,
    val isReliable: Boolean,
    val address: String,
    val amount: BigDecimal?
)

internal enum class StakeType {
    @SerializedName("1to1")
    OneStakeToOneValidator,

    @SerializedName("Nto1")
    MultiStakeToOneValidator,

    @SerializedName("1toN")
    OneStakeToMultipleValidators,
    Unknown
}