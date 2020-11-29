package com.everstake.staking.sdk.ui.coin.list

import android.view.View
import com.bumptech.glide.Glide
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinListModel
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder
import com.everstake.staking.sdk.util.bindString
import kotlinx.android.synthetic.main.item_coin_info.view.*

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal class CoinListViewHolder(itemView: View) : BaseViewHolder<CoinListModel>(itemView) {
    override fun bind(model: CoinListModel) {
        val (_, name, iconUrl, apr, isActive, stakedAmount) = model
        itemView.stakeCoinContainer.apply {
            setOnClickListener {
                this@CoinListViewHolder.clickListener?.onClick(
                    adapterPosition,
                    model
                )
            }
            isEnabled = isActive
            alpha = if (isActive) 1F else 0.4F
        }

        Glide.with(itemView).load(iconUrl).into(itemView.stakeCoinImage)
        itemView.stakeCoinTitle.text = name

        val subtitle: String = if (isActive) apr
        else bindString(itemView.context, R.string.coin_list_coming_soon)
        itemView.stakeCoinSubtitle.text = subtitle

        itemView.stakeCoinAmount.visibility =
            if (stakedAmount.isNullOrBlank()) View.GONE else View.VISIBLE
        itemView.stakeCoinAmount.text = stakedAmount
    }

    override fun clearUp() {
        super.clearUp()
        Glide.with(itemView).clear(itemView.stakeCoinImage)
    }
}