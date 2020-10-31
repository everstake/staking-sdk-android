package com.everstake.staking.sdk.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.everstake.staking.sdk.EverstakeStaking
import kotlin.math.roundToInt

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal fun bindString(
    context: Context,
    @StringRes res: Int,
    vararg args: Any = arrayOf()
): String =
    context.getString(res, *args)

internal fun bindPlural(
    context: Context,
    @PluralsRes res: Int, amount: Int, vararg args: Any = arrayOf(amount)
): String = context.resources.getQuantityString(res, amount, *args)

internal fun bindColor(
    context: Context,
    @ColorRes res: Int
): Int = ContextCompat.getColor(context, res)

internal fun dpToPx(dp: Int): Int = dpToPx(dp.toFloat())

internal fun dpToPx(dp: Float): Int =
    (dp * EverstakeStaking.app.resources.displayMetrics.density).roundToInt()

internal fun spToPx(sp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    sp.toFloat(), EverstakeStaking.app.resources.displayMetrics
)
