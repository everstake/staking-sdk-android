package com.everstake.staking.sdk.ui.base.list.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import com.everstake.staking.sdk.EverstakeStaking
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.dpToPx

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal class DividerDecorator(
    private val marginLeft: Int = 0,
    private val marginRight: Int = 0,
    private val drawLast: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left: Int = marginLeft
        val right: Int = parent.width - marginRight
        val drawable: Drawable = getDividerDrawable(parent.context)

        val childCount: Int = if (drawLast) parent.childCount else parent.childCount - 1

        (0 until childCount).forEach { childPos: Int ->
            val child = parent.getChildAt(childPos)
            (child.layoutParams as? RecyclerView.LayoutParams)?.let {
                val top = child.bottom + it.bottomMargin
                val bottom = top + drawable.intrinsicHeight
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(c)
            }
        }
    }

    private fun getDividerDrawable(context: Context = EverstakeStaking.app): Drawable {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setSize(-1, dpToPx(1))
        drawable.setColor(bindColor(context, R.color.everstakeDivider))
        return drawable
    }
}