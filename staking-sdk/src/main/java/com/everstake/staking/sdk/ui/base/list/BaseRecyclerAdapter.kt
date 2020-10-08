package com.everstake.staking.sdk.ui.base.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.everstake.staking.sdk.data.model.DiffCompared
import com.everstake.staking.sdk.util.DiffList

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal abstract class BaseRecyclerAdapter<M : DiffCompared> :
    RecyclerView.Adapter<BaseViewHolder<M>>() {

    companion object {
        const val DEFAULT_VIEW_TYPE = 0
    }

    val modelsList: MutableList<M> = ArrayList()
    var clickListenerRef: RecyclerClickListener<M>? = null

    fun setClickListener(clickListener: RecyclerClickListener<M>?) {
        if (clickListenerRef == clickListener) return
        clickListenerRef = clickListener
        notifyDataSetChanged()
    }

    // Diff calculation is time-consuming process
    suspend fun applyChanges(models: List<M>) {
        DiffList.merge(this, models)
    }

    fun setNewData(models: List<M>) {
        this.modelsList.clear()
        this.modelsList.addAll(models)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return provideViewHolder(inflater, parent, viewType)
    }

    abstract fun provideViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<M>

    open override fun getItemViewType(position: Int): Int = DEFAULT_VIEW_TYPE

    open override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        if (position !in (0 until modelsList.size)) return
        bindHolder(holder, position, modelsList[position])
    }

    open fun bindHolder(holder: BaseViewHolder<M>, position: Int, model: M) {
        holder.bind(model, clickListenerRef)
    }

    open override fun getItemCount(): Int = modelsList.size

    override fun onViewRecycled(holder: BaseViewHolder<M>) {
        super.onViewRecycled(holder)
        holder.clearUp()
    }


}