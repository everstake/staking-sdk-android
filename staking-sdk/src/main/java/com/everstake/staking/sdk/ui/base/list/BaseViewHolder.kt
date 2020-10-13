package com.everstake.staking.sdk.ui.base.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal abstract class BaseViewHolder<M>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var model: M? = null
    protected var clickListener: RecyclerClickListener<M>? = null

    fun bind(model: M, clickListener: RecyclerClickListener<M>?) {
        this.clearUp()
        this.clickListener = clickListener
        this.model = model
        bind(model)
    }

    abstract fun bind(model: M)

    open fun clearUp() {
        clickListener = null
    }
}