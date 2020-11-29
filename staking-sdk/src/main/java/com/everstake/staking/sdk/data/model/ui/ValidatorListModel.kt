package com.everstake.staking.sdk.data.model.ui

import com.everstake.staking.sdk.data.model.DiffCompared

/**
 * created by Alex Ivanov on 24.10.2020.
 */
internal data class ValidatorListModel(
    val id: String,
    val isSelected: Boolean,
    val name: String,
    val fee: String,
    val isReliable: Boolean
) : DiffCompared {
    override fun idEquals(obj: Any): Boolean {
        if (obj !is ValidatorListModel) return false
        return this.id == obj.id
    }

    override fun uiEquals(obj: Any): Boolean {
        if (obj !is ValidatorListModel) return false
        return this.id == obj.id
                && this.isSelected == obj.isSelected
                && this.name == obj.name
                && this.fee == obj.fee
                && this.isReliable == obj.isReliable
    }
}