package com.everstake.staking.sdk.ui.validator.select

import android.util.TypedValue
import android.view.View
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder
import com.everstake.staking.sdk.util.bindString
import kotlinx.android.synthetic.main.item_validator_info.view.*

/**
 * created by Alex Ivanov on 24.10.2020.
 */
internal class ValidatorSelectViewHolder(itemView: View) :
    BaseViewHolder<ValidatorListModel>(itemView) {

    override fun bind(model: ValidatorListModel) {
        val (_, isSelected: Boolean, name: String, fee: String, isReliable: Boolean) = model

        itemView.validatorInfoContainer.setOnClickListener {
            clickListener?.onClick(adapterPosition, model)
        }
        itemView.validatorInfoSelectedImg.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE

        itemView.validatorInfoTitle.text = name
        itemView.validatorInfoSubtitle.text =
            bindString(itemView.context, R.string.common_fee_format, fee)

        if (isReliable) {
            itemView.validatorInfoContainer.setBackgroundResource(R.drawable.reliable_validator_bg)
            itemView.validatorInfoReliable.visibility = View.VISIBLE
        } else {
            itemView.validatorInfoReliable.visibility = View.GONE
            with(TypedValue()) {
                itemView.context.theme.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    this,
                    true
                )
                itemView.validatorInfoContainer.setBackgroundResource(resourceId)
            }
        }
    }
}