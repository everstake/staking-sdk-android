package com.everstake.staking.sdk.ui.validator.select

import android.view.LayoutInflater
import android.view.ViewGroup
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.ui.base.list.BaseRecyclerAdapter
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder

/**
 * created by Alex Ivanov on 24.10.2020.
 */
internal class ValidatorSelectAdapter : BaseRecyclerAdapter<ValidatorListModel>() {
    override fun provideViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ValidatorListModel> = ValidatorSelectViewHolder(
        layoutInflater.inflate(
            R.layout.item_validator_info,
            parent,
            false
        )
    )
}