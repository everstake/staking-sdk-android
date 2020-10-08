package com.everstake.staking.sdk.data.model.ui

import com.everstake.staking.sdk.data.model.DiffCompared

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal data class CoinListModel(
    val id: String,
    val name: String,
    val iconUrl: String,
    val apr: String,
    val isActive: Boolean,
    val stakedAmount: String?
) : DiffCompared {
    override fun idEquals(obj: Any): Boolean {
        if (obj !is CoinListModel) return false
        return this.id == obj.id
    }

    override fun uiEquals(obj: Any): Boolean {
        if (obj !is CoinListModel) return false
        return this.id == obj.id
                && this.name == obj.name
                && this.iconUrl == obj.iconUrl
                && this.apr == obj.apr
                && this.isActive == obj.isActive
                && this.stakedAmount == obj.stakedAmount
    }
}