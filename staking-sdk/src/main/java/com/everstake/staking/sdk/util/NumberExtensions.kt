package com.everstake.staking.sdk.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * created by Alex Ivanov on 12.10.2020.
 */
/**
 * Method which used to force DecimalFormat to use English number formatting
 *
 * @return DecimalFormatSymbols setup for english locale
 */
private fun provideSymbolsFormat(): DecimalFormatSymbols =
    DecimalFormatSymbols.getInstance(Locale.ENGLISH)


fun Number.format(formatPattern: String): String =
    DecimalFormat(formatPattern, provideSymbolsFormat()).apply {
        roundingMode = RoundingMode.FLOOR
    }.format(this)
