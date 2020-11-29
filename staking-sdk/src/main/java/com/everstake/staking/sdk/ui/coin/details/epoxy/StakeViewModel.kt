package com.everstake.staking.sdk.ui.coin.details.epoxy

import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.data.model.ui.CoinDetailsValidatorInfo
import com.everstake.staking.sdk.ui.base.list.BaseEpoxyHolder
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.bindString
import com.everstake.staking.sdk.util.getDataInfoSpan

/**
 * created by Alex Ivanov on 11/29/20.
 */
@EpoxyModelClass
internal abstract class StakeViewModel : EpoxyModelWithHolder<StakeHolder>() {
    @EpoxyAttribute
    var totalStakedAmount: String = ""

    @EpoxyAttribute
    var validators: List<CoinDetailsValidatorInfo> = emptyList()

    @EpoxyAttribute(DoNotHash)
    var unstakeClickListener: ((idList: List<String>) -> Unit)? = null


    override fun getDefaultLayout(): Int = R.layout.view_stake_detail

    override fun bind(holder: StakeHolder) {
        super.bind(holder)
        holder.unstakeButton.apply {
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = 0
            }
            setOnClickListener {
                unstakeClickListener?.invoke(validators.map { it.validatorId })
            }
        }
        holder.stakedAmountText.text = totalStakedAmount
        holder.stakeValidatorName.apply {
            text = getDataInfoSpan(
                bindString(context, R.string.coin_details_validator),
                validators.joinToString(", ") { it.validatorName },
                bindColor(context, R.color.everstakeTextColorPrimary)
            )
        }
    }

    override fun unbind(holder: StakeHolder) {
        super.unbind(holder)
        holder.unstakeButton.setOnClickListener(null)
    }
}

@EpoxyModelClass
internal abstract class SingleStakeViewModel : EpoxyModelWithHolder<StakeHolder>() {
    @EpoxyAttribute
    lateinit var validator: CoinDetailsValidatorInfo

    @EpoxyAttribute(DoNotHash)
    var unstakeClickListener: ((idList: List<String>) -> Unit)? = null


    override fun getDefaultLayout(): Int = R.layout.view_stake_detail

    override fun bind(holder: StakeHolder) {
        super.bind(holder)
        holder.unstakeButton.apply {
            setOnClickListener {
                unstakeClickListener?.invoke(listOf(validator.validatorId))
            }
        }
        holder.stakedAmountText.text = validator.stakedAmount
        holder.stakeValidatorName.apply {
            text = getDataInfoSpan(
                bindString(context, R.string.coin_details_validator),
                validator.validatorName,
                bindColor(context, R.color.everstakeTextColorPrimary)
            )
        }
    }

    override fun unbind(holder: StakeHolder) {
        super.unbind(holder)
        holder.unstakeButton.setOnClickListener(null)
    }
}


internal class StakeHolder : BaseEpoxyHolder() {
    val stakedAmountText: TextView by bind(R.id.coinDetailsStakedAmount)
    val unstakeButton: Button by bind(R.id.coinDetailsUnstakeButton)
    val stakeValidatorName: TextView by bind(R.id.coinDetailsStakedValidator)
}