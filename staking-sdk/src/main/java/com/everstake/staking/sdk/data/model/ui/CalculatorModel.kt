package com.everstake.staking.sdk.data.model.ui

/**
 * created by Alex Ivanov on 26.10.2020.
 */
data class CalculatorModel(
    val coinId: String,
    val coinName:String,
    val coinSymbol: String,
    val coinYearlyIncomePercent: String,
    val validatorId: String,
    val validatorName: String,
    val validatorFee:String,
    val isReliableValidator: Boolean,
    val dailyIncome: String,
    val monthlyIncome: String,
    val yearlyIncome: String
)