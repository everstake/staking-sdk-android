package com.everstake.staking.sdk.util

import java.math.BigDecimal

/**
 * created by Alex Ivanov on 12.10.2020.
 */
private fun provideFormat(precision: Int): String = (0 until precision).fold("0") { acc, i ->
    acc + when (i) {
        0 -> ".#"
        else -> "#"
    }
}

fun formatAmount(amount: BigDecimal, precision: Int, symbol: String): String =
    "${amount.format(provideFormat(precision))} $symbol"