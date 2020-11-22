package com.everstake.staking.sdk.data.model.ui

import java.math.BigDecimal

/**
 * created by Alex Ivanov on 02.11.2020.
 */
internal data class StakeModel(
    val coinId: String,
    val balance: String,
    val amount: String,
    // [0,1]
    val progress: BigDecimal,
    val coinSymbol: String,
    val coinYearlyIncomePercent: String,
    val validatorId: String,
    val validatorName: String,
    val validatorAddress: String,
    val validatorFee: String,
    val isReliableValidator: Boolean,
    val dailyIncome: String,
    val monthlyIncome: String,
    val yearlyIncome: String
)