package com.everstake.staking.sdk.ui.coin.select

import android.view.View
import com.bumptech.glide.Glide
import com.everstake.staking.sdk.data.model.ui.CoinSelectModel
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder
import kotlinx.android.synthetic.main.item_coin_info.view.*

/**
 * created by Alex Ivanov on 23.10.2020.
 */
internal class CoinSelectViewHolder(itemView: View) : BaseViewHolder<CoinSelectModel>(itemView) {

    override fun bind(model: CoinSelectModel) {
        val (_, name, iconUrl, apr) = model

        itemView.stakeCoinContainer.setOnClickListener {
            this.clickListener?.onClick(adapterPosition, model)
        }
        Glide.with(itemView).load(iconUrl).into(itemView.stakeCoinImage)
        itemView.stakeCoinTitle.text = name
        itemView.stakeCoinSubtitle.text = apr
        itemView.stakeCoinAmount.visibility = View.GONE
    }

    override fun clearUp() {
        super.clearUp()
        Glide.with(itemView).clear(itemView.stakeCoinImage)
    }
}