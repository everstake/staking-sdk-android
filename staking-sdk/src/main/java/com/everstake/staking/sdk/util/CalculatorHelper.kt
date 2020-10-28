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
        // Just a random precision number
        private const val INTERVAL_SCALE: Int = 5
    }

    fun calculate(duration: BigInteger, periodDuration: BigInteger): BigDecimal {
        // Calculate number of periods.
        // Set Scale is required because of how division works in BigDecimal (in our case scale will be 0 and numbers will be divided like integers)
        return calculate(
            duration.toBigDecimal().setScale(INTERVAL_SCALE).div(periodDuration.toBigDecimal())
        )
    }

    private fun calculate(periodCount: BigDecimal): BigDecimal {
        var periodIncome: BigDecimal = BigDecimal.ZERO
        if (this.includeReinvestment) {
            var count: BigDecimal = periodCount
            while (count > BigDecimal.ZERO) {
                val periodSize: BigDecimal = count.min(BigDecimal.ONE)
                // With reinvest amount will change every time user will be able to claim
                val periodAmount: BigDecimal = amount + periodIncome
                periodIncome += periodAmount * periodScale * periodSize
                count -= periodSize
            }
        } else {
            // We can speed up calculation in this case because amount is static
            periodIncome = amount * periodScale * periodCount
        }
        return periodIncome
    }
}