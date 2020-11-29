package com.everstake.staking.sdk.data.model.ui

import androidx.annotation.StringRes

/**
 * created by Alex Ivanov on 10.10.2020.
 */
internal data class SectionData<T>(
    @StringRes val sectionTitleRes: Int,
    val sectionData: List<T>
)