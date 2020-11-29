package com.everstake.staking.sdk.ui.coin.select

import android.view.LayoutInflater
import android.view.ViewGroup
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinSelectModel
import com.everstake.staking.sdk.ui.base.list.BaseRecyclerAdapter
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class CoinSelectAdapter : BaseRecyclerAdapter<CoinSelectModel>() {
    override fun provideViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<CoinSelectModel> =
        CoinSelectViewHolder(layoutInflater.inflate(R.layout.item_coin_info, parent, false))
}