package com.everstake.staking.sdk.util

import androidx.recyclerview.widget.DiffUtil
import com.everstake.staking.sdk.data.model.DiffCompared
import com.everstake.staking.sdk.ui.base.list.BaseRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal object DiffList {
    suspend fun <M : DiffCompared> merge(
        adapter: BaseRecyclerAdapter<M>,
        newList: List<M>
    ) {
        val result: DiffUtil.DiffResult = suspendCoroutine { continuation ->
            val old: List<M> = adapter.modelsList.toList()
            continuation.resume(DiffUtil.calculateDiff(DiffCallback(old, newList)))
        }
        withContext(Dispatchers.Main) {
            adapter.modelsList.apply {
                clear()
                addAll(newList)
            }
            result.dispatchUpdatesTo(adapter)
        }
    }
}

internal class DiffCallback(
    private val oldList: List<DiffCompared>,
    private val newList: List<DiffCompared>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].idEquals(newList[newItemPosition])

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].uiEquals(newList[newItemPosition])
}
