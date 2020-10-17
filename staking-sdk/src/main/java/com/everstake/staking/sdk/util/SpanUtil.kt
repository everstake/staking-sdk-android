package com.everstake.staking.sdk.util

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

/**
 * created by Alex Ivanov on 17.10.2020.
 */
internal fun getDataInfoSpan(info: String, data: String, @ColorInt dataColor: Int): Spannable =
    SpannableStringBuilder(info)
        .append(' ')
        .append(data, ForegroundColorSpan(dataColor), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)