package com.everstake.staking.sdk.ui.validator.select

import android.view.View
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.ValidatorListModel
import com.everstake.staking.sdk.ui.base.list.BaseViewHolder
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.setSelectableItemBackground
import kotlinx.android.synthetic.main.item_validator_info.view.*

/**
 * created by Alex Ivanov on 24.10.2020.
 */
internal class ValidatorSelectViewHolder(itemView: View, private val multiSelect: Boolean) :
    BaseViewHolder<ValidatorListModel>(itemView) {

    override fun bind(model: ValidatorListModel) {
        val (_, isSelected: Boolean, name: String, fee: String, isReliable: Boolean) = model
        itemView.validatorInfoCheckbox.setOnCheckedChangeListener(null)
        itemView.validatorInfoContainer.setOnClickListener {
            if (multiSelect) {
                itemView.validatorInfoCheckbox.toggle()
            } else {
                clickListener?.onClick(adapterPosition, model)
            }
        }
        if (multiSelect) {
            itemView.validatorInfoSelectedImg.visibility = View.GONE
            itemView.validatorInfoSubtitle.visibility = View.GONE
            itemView.validatorInfoCheckbox.apply {
                visibility = View.VISIBLE
                isChecked = isSelected
            }
        } else {
            itemView.validatorInfoSelectedImg.visibility =
                if (isSelected) View.VISIBLE else View.INVISIBLE
            itemView.validatorInfoSubtitle.visibility = View.VISIBLE
            itemView.validatorInfoCheckbox.visibility = View.GONE
        }

        itemView.validatorInfoTitle.text = name
        itemView.validatorInfoSubtitle.text =
            bindString(itemView.context, R.string.common_fee_format, fee)

        if (isReliable) {
            itemView.validatorInfoContainer.setBackgroundResource(R.drawable.reliable_validator_bg)
            itemView.validatorInfoReliable.visibility = View.VISIBLE
        } else {
            itemView.validatorInfoReliable.visibility = View.GONE
            itemView.validatorInfoContainer.setSelectableItemBackground()
        }

        if (multiSelect) {
            itemView.validatorInfoCheckbox.setOnCheckedChangeListener { _, isChecked: Boolean ->
                clickListener?.onClick(adapterPosition, model.copy(isSelected = isChecked))
            }
        }
    }
}