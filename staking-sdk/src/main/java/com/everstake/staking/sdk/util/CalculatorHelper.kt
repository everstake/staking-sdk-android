package com.everstake.staking.sdk.util

import java.math.BigDecimal
import java.math.BigInteger

/**
 * created by Alex Ivanov on 26.10.2020.
 */
internal class CalculatorHelper(
    private val amount: BigDecimal,
    private val periodScale: BigDecimal,
    private val includeReinvestment: Boolean
) {
    companion object {
        private const val INTERVAL_SCALE: Int = 5
    }

    fun calculate(duration: BigInteger, periodDuration: BigInteger): BigDecimal {
        return calculate(
            duration.toBigDecimal().setScale(INTERVAL_SCALE).div(periodDuration.toBigDecimal())
        )
    }

    fun calculate(periodCount: BigDecimal): BigDecimal {
        var count: BigDecimal = periodCount
        var income: BigDecimal = BigDecimal.ZERO
        while (count > BigDecimal.ZERO) {
            val periodSize: BigDecimal = count.min(BigDecimal.ONE)
            val periodAmount: BigDecimal = if (includeReinvestment) amount + income else amount
            income += periodAmount * periodScale * periodSize
            count -= periodSize
        }
        return income
    }
}