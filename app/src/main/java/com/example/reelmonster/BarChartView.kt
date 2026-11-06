package com.example.reelmonster

import android.content.Context
import android.graphics.*
import android.view.View
import java.time.format.TextStyle
import java.util.Locale

/** Simple bar chart of the last 7 days. */
class BarChartView(c: Context) : View(c) {
    var data: List<Pair<java.time.LocalDate, Int>> = emptyList()
        set(v) { field = v; invalidate() }
    private val d = resources.displayMetrics.density
    private val bar = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFC8325F.toInt() }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF555555.toInt(); textSize = 11 * d; textAlign = Paint.Align.CENTER
    }

    override fun onDraw(cv: Canvas) {
        if (data.isEmpty()) return
        val max = (data.maxOf { it.second }).coerceAtLeast(1)
        val slot = width / data.size.toFloat()
        val chartH = height - 34 * d
        data.forEachIndexed { i, (day, n) ->
            val h = chartH * n / max
            val l = i * slot + slot * 0.2f
            cv.drawRoundRect(RectF(l, chartH - h + 16 * d, l + slot * 0.6f, chartH + 16 * d), 6 * d, 6 * d, bar)
            cv.drawText(n.toString(), l + slot * 0.3f, chartH - h + 12 * d, label)
            cv.drawText(day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                l + slot * 0.3f, height - 4 * d, label)
        }
    }
}
