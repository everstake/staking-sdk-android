package com.everstake.staking.sdk.util

import android.util.TypedValue
import android.view.View

/**
 * created by Alex Ivanov on 25.10.2020.
 */
internal fun View.setSelectableItemBackground() {
    val typedValue = TypedValue()
    this.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
    this.setBackgroundResource(typedValue.resourceId)
}