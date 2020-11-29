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
internal abstract class AboutViewModel : EpoxyModelWithHolder<AboutViewModel.AboutHolder>() {
    @EpoxyAttribute
    var about: String? = null

    override fun getDefaultLayout(): Int = R.layout.view_details_about

    override fun bind(holder: AboutHolder) {
        super.bind(holder)
        holder.aboutContainer.visibility = if (about.isNullOrBlank()) View.GONE else View.VISIBLE
        holder.aboutText.text = about
    }

    inner class AboutHolder : BaseEpoxyHolder() {
        val aboutContainer: View by bind(R.id.coinDetailsAboutContainer)
        val aboutText: TextView by bind(R.id.coinDetailsAboutText)
    }
}