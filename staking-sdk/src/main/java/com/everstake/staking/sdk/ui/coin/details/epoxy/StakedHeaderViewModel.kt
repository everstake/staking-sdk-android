package com.everstake.staking.sdk.ui.coin.details.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ui.base.list.BaseEpoxyHolder

/**
 * created by Alex Ivanov on 11/29/20.
 */
@EpoxyModelClass
internal abstract class StakedHeaderViewModel :
    EpoxyModelWithHolder<StakedHeaderViewModel.StakedHeaderHolder>() {
    @EpoxyAttribute
    var showList: Boolean = false

    override fun getDefaultLayout(): Int = R.layout.view_details_staked_header

    override fun bind(holder: StakedHeaderHolder) {
        super.bind(holder)
        holder.headerLabel.setText(if (showList) R.string.coin_details_staked_list else R.string.coin_details_staked)
        holder.headerDivider.visibility = if (showList) View.VISIBLE else View.GONE
    }

    inner class StakedHeaderHolder : BaseEpoxyHolder() {
        val headerLabel: TextView by bind(R.id.coinDetailsStakeHeader)
        val headerDivider: View by bind(R.id.stakeHeaderDivider)
    }
}