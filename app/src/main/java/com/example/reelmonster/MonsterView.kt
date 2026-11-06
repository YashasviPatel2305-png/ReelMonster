package com.example.reelmonster

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.min

/** Cute monster drawn with Canvas. Grows as the count rises. */
class MonsterView(c: Context) : View(c) {
    var count = 0
        set(v) { field = v; invalidate() }

    private val d = resources.displayMetrics.density
    private val body = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFC8325F.toInt() }
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFF2D6DE.toInt(); style = Paint.Style.STROKE; strokeWidth = 4 * d
    }
    private val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
    private val black = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
    private val mouth = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 3 * d; strokeCap = Paint.Cap.ROUND
    }
    private val badge = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFF2C94C.toInt() }
    private val txt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD
    }

    override fun onMeasure(w: Int, h: Int) {
        val s = (130 * d).toInt()
        setMeasuredDimension(s, s)
    }

    override fun onDraw(cv: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val grow = 0.55f + min(count, 150) / 150f * 0.45f
        val r = width * 0.40f * grow

        cv.drawCircle(cx - r * 0.6f, cy - r * 0.85f, r * 0.2f, body) // ears
        cv.drawCircle(cx + r * 0.6f, cy - r * 0.85f, r * 0.2f, body)
        cv.drawCircle(cx, cy, r, body)
        cv.drawCircle(cx, cy, r, ring)

        val ex = r * 0.32f
        val ey = cy - r * 0.15f
        cv.drawCircle(cx - ex, ey, r * 0.22f, white)
        cv.drawCircle(cx + ex, ey, r * 0.22f, white)
        cv.drawCircle(cx - ex, ey, r * 0.10f, black)
        cv.drawCircle(cx + ex, ey, r * 0.10f, black)

        cv.drawArc(RectF(cx - r * 0.3f, cy + r * 0.0f, cx + r * 0.3f, cy + r * 0.5f), 20f, 140f, false, mouth)

        val bx = cx + r * 0.72f
        val by = cy + r * 0.72f
        val br = 15 * d
        cv.drawCircle(bx, by, br, badge)
        txt.textSize = (if (count > 99) 11 else 14) * d
        cv.drawText(count.toString(), bx, by + txt.textSize * 0.35f, txt)
    }
}
