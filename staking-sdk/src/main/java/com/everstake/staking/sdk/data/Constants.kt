package com.everstake.staking.sdk.data

import java.math.BigInteger

/**
 * created by Alex Ivanov on 27.10.2020.
 */
internal object Constants {
    const val EVERSTAKE_URL: String = "https://everstake.one/"

    val DAY_IN_SECONDS: BigInteger = 86400.toBigInteger()
    val MONTH_IN_SECONDS: BigInteger = DAY_IN_SECONDS * 30.toBigInteger()
    val YEAR_IN_SECONDS: BigInteger = DAY_IN_SECONDS * 365.toBigInteger()

    const val PROGRESS_MAX_VALUE: Int = 1000

    const val MAX_DISPLAY_PRECISION: Int = 8
}