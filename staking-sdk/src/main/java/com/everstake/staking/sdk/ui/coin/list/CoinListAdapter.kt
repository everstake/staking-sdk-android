package com.everstake.staking.sdk.ui.coin.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.ui.base.list.BaseRecyclerAdapter
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal class CoinListAdapter : BaseRecyclerAdapter<CoinListModel>() {

    override fun provideViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<CoinListModel> =
        CoinListViewHolder(layoutInflater.inflate(R.layout.item_coin_info, parent, false))
}