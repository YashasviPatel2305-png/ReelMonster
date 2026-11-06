package com.example.reelmonster

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private lateinit var countTv: TextView
    private lateinit var lostTv: TextView
    private lateinit var lineTv: TextView
    private lateinit var chart: BarChartView
    private lateinit var permBtn: Button
    private lateinit var toggleBtn: Button
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() { refresh(); handler.postDelayed(this, 1000) }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        if (Build.VERSION.SDK_INT >= 33)
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(28), dp(20), dp(20))
            setBackgroundColor(Color.parseColor("#F6F1F3"))
        }
        fun text(size: Float, bold: Boolean = false, color: Int = Color.BLACK) = TextView(this).apply {
            textSize = size; setTextColor(color); gravity = Gravity.CENTER
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
        col.addView(text(14f, true, Color.parseColor("#C8325F")).apply { this.text = "TODAY" })
        countTv = text(64f, true); col.addView(countTv)
        lostTv = text(16f, false, Color.GRAY); col.addView(lostTv)
        col.addView(text(13f, false, Color.GRAY).apply { this.text = "Last 7 days"; setPadding(0, dp(18), 0, 0) })
        chart = BarChartView(this)
        col.addView(chart, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(180)))
        lineTv = text(16f, true).apply { setPadding(dp(12), dp(14), dp(12), dp(14)) }
        col.addView(lineTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(14) })

        permBtn = Button(this).apply {
            text = "1. Allow \"Display over other apps\""
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
        }
        toggleBtn = Button(this).apply {
            setOnClickListener {
                if (Store.isEnabled(this@MainActivity)) {
                    stopService(Intent(this@MainActivity, BubbleService::class.java))
                    Store.setEnabled(this@MainActivity, false)
                } else {
                    startForegroundService(Intent(this@MainActivity, BubbleService::class.java))
                    Store.setEnabled(this@MainActivity, true)
                }
                refresh()
            }
        }
        col.addView(permBtn); col.addView(toggleBtn)
        setContentView(ScrollView(this).apply { addView(col) })
    }

    private fun refresh() {
        val n = Store.today(this)
        countTv.text = n.toString()
        lostTv.text = "reels · ${Store.timeLost(n)} lost"
        lineTv.text = Store.lineFor(n)
        chart.data = Store.last7(this)
        val canDraw = Settings.canDrawOverlays(this)
        permBtn.visibility = if (canDraw) View.GONE else View.VISIBLE
        toggleBtn.isEnabled = canDraw
        toggleBtn.text = if (Store.isEnabled(this)) "Stop the monster" else "2. Start the monster"
    }

    override fun onResume() { super.onResume(); handler.post(tick) }
    override fun onPause() { super.onPause(); handler.removeCallbacks(tick) }
}
