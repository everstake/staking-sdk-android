package com.everstake.staking.sdk.util

import com.everstake.staking.sdk.data.Constants.MAX_DISPLAY_PRECISION
import java.math.BigDecimal
import java.util.*
import kotlin.math.min

/**
 * created by Alex Ivanov on 12.10.2020.
 */
private fun provideFormat(precision: Int): String = (0 until precision).fold("0") { acc, i ->
    acc + when (i) {
        0 -> ".#"
        else -> "#"
    }
}

internal fun formatAmount(amount: BigDecimal, precision: Int, symbol: String? = null): String {
    val displayPrecision: Int = min(precision, MAX_DISPLAY_PRECISION)
    return if (symbol == null) amount.format(provideFormat(precision))
    else "${amount.format(provideFormat(displayPrecision))} $symbol"
}
