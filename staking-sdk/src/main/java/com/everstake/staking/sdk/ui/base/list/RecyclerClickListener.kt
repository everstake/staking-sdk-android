package com.everstake.staking.sdk.ui.base.list

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal open class RecyclerClickListener<in M> {
    open fun onClick(pos: Int, model: M?) {}
    open fun onLongClick(pos: Int, model: M?) {}
}