package com.everstake.staking.sdk.ui.base.list.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.everstake.staking.sdk.R
import com.everstake.staking.sdk.util.bindColor
import com.everstake.staking.sdk.util.dpToPx
import com.everstake.staking.sdk.util.spToPx
import kotlin.math.abs

/**
 * created by Alex Ivanov on 08.10.2020.
 */
internal class TextDividerDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private val textPaddingHorizontal: Int = dpToPx(16)
    private val textPaddingVertical: Int = dpToPx(16)
    private val textPaint: Paint = Paint().apply {
        color = bindColor(context, R.color.everstakeTextColorPrimary)
        alpha = 0x66 //40% opaque
        textSize = spToPx(14)
    }.also { paint ->
        val fontMetrics: Paint.FontMetricsInt = paint.fontMetricsInt
        textHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        textBaseLine = abs(fontMetrics.ascent)
    }
    private var textHeight: Int = 0

    // Top to baseline distance
    private var textBaseLine: Int = 0
    private val data: MutableMap<Int, String> = mutableMapOf()

    fun setData(dataSet: List<DecoratorData>) {
        data.clear()
        dataSet.forEach { (adapterPosition: Int, text: String) -> data[adapterPosition] = text }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapterPosition: Int = parent.getChildAdapterPosition(view)
        if (data.containsKey(adapterPosition)) {
            outRect.top = textHeight + textPaddingVertical * 2
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        (0 until parent.childCount).forEach { index: Int ->
            val child: View = parent.getChildAt(index)
            val adapterPosition: Int = parent.getChildAdapterPosition(child)
            if (data.containsKey(adapterPosition)) {
                val text: String = data[adapterPosition] ?: ""
                c.drawText(
                    text,
                    textPaddingHorizontal.toFloat(),
                    child.top.toFloat() - (textHeight - textBaseLine) / 2 - textPaddingVertical,
                    textPaint
                )
            }
        }
    }
}

internal data class DecoratorData(val adapterPosition: Int, val text: String)