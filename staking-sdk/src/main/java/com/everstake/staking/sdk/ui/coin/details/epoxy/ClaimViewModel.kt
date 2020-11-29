package com.everstake.staking.sdk.ui.coin.details.epoxy

import android.widget.Button
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.ui.base.list.BaseEpoxyHolder
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.getDataInfoSpan

/**
 * created by Alex Ivanov on 11/29/20.
 */
@EpoxyModelClass
internal abstract class ClaimViewModel : EpoxyModelWithHolder<ClaimViewModel.ClaimHolder>() {
    @EpoxyAttribute
    var claimAmount: String = ""

    @EpoxyAttribute
    var coinSymbol: String = ""

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var claimClickListener: (() -> Unit)? = null

    override fun getDefaultLayout(): Int = R.layout.view_details_claim

    override fun bind(holder: ClaimHolder) {
        super.bind(holder)
        holder.availableClaimText.apply {
            text = getDataInfoSpan(
                bindString(context, R.string.coin_details_available_rewards),
                "$claimAmount $coinSymbol",
                bindColor(context, R.color.everstakeTextColorPrimary)
            )
        }
        holder.claimButton.setOnClickListener {
            claimClickListener?.invoke()
        }
    }

    override fun unbind(holder: ClaimHolder) {
        super.unbind(holder)
        holder.claimButton.setOnClickListener(null)
    }

    inner class ClaimHolder : BaseEpoxyHolder() {
        val availableClaimText: TextView by bind(R.id.coinDetailsAvailableClaim)
        val claimButton: Button by bind(R.id.coinDetailsStakeClaimButton)
    }
}