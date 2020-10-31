package com.everstake.staking.sdk.util

import android.content.Context
import com.everstake.staking.sdk.R
import java.util.*

/**
 * created by Alex Ivanov on 31.10.2020.
 */
internal fun formatTimeSeconds(context: Context, seconds: Long): String {
    return formatTime(context, seconds * 1000L)
}

internal fun formatTime(context: Context, millis: Long): String {
    val date: Calendar = Calendar.getInstance()
    date.timeInMillis = millis
    date.timeZone = TimeZone.getDefault()
    date.add(Calendar.MILLISECOND, -date.timeZone.rawOffset)
    if (!date.timeZone.inDaylightTime(date.time)) date.add(
        Calendar.MILLISECOND,
        -date.timeZone.dstSavings
    )

    val days = date.get(Calendar.DAY_OF_YEAR) - 1
    val hours = date.get(Calendar.HOUR)
    val minutes = date.get(Calendar.MINUTE)
    val seconds = date.get(Calendar.SECOND)
    return ((if (days > 0) "${bindPlural(context, R.plurals.common_day, days)} " else "") +
            (if (hours > 0) "${bindPlural(context, R.plurals.common_hour, hours)} " else "") +
            (if (minutes > 0) "${bindPlural(context, R.plurals.common_minute, minutes)} " else "") +
            (if (seconds > 0) bindPlural(context, R.plurals.common_second, seconds) else "")
            ).trimEnd()
}