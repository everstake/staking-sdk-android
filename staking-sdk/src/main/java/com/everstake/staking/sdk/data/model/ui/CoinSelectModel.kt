package com.everstake.staking.sdk.data.model.ui

import com.everstake.staking.sdk.data.model.DiffCompared

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal data class CoinSelectModel(
    val id: String,
    val name: String,
    val iconUrl: String,
    val apr: String
) : DiffCompared {
    override fun idEquals(obj: Any): Boolean {
        if (obj !is CoinSelectModel) return false
        return this.id == obj.id
    }

    override fun uiEquals(obj: Any): Boolean {
        if (obj !is CoinSelectModel) return false
        return this.id == obj.id
                && this.name == obj.name
                && this.iconUrl == obj.iconUrl
                && this.apr == obj.apr
    }
}