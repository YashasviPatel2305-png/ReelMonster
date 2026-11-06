package com.example.reelmonster

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.abs

/** Foreground service that shows the floating monster above all other apps. */
class BubbleService : Service() {
    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private lateinit var monster: MonsterView
    private lateinit var speech: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val resetHint = Runnable { speech.text = "Tap me +1" }

    override fun onBind(i: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startAsForeground()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        if (Settings_canDraw()) addBubble()
    }

    private fun Settings_canDraw() = android.provider.Settings.canDrawOverlays(this)

    private fun startAsForeground() {
        val ch = "reel_monster"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel(ch, "Reel Monster", NotificationManager.IMPORTANCE_LOW))
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val n = Notification.Builder(this, ch)
            .setContentTitle("Reel Monster is watching")
            .setContentText("Tap the monster after each reel")
            .setSmallIcon(android.R.drawable.star_on)
            .setContentIntent(pi)
            .build()
        if (Build.VERSION.SDK_INT >= 34)
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        else startForeground(1, n)
    }

    private fun addBubble() {
        val d = resources.displayMetrics.density
        monster = MonsterView(this).apply { count = Store.today(this@BubbleService) }
        speech = TextView(this).apply {
            text = "Tap me +1"
            textSize = 12f
            setTextColor(Color.BLACK)
            setPadding((10 * d).toInt(), (5 * d).toInt(), (10 * d).toInt(), (5 * d).toInt())
            background = GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = 14 * d }
        }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(monster)
            addView(speech)
        }
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = (16 * d).toInt(); y = (160 * d).toInt() }

        var sx = 0; var sy = 0; var tx = 0f; var ty = 0f; var moved = false
        root!!.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { sx = lp.x; sy = lp.y; tx = e.rawX; ty = e.rawY; moved = false }
                MotionEvent.ACTION_MOVE -> {
                    val dx = e.rawX - tx; val dy = e.rawY - ty
                    if (abs(dx) > 12 * d || abs(dy) > 12 * d) moved = true
                    if (moved) { lp.x = sx + dx.toInt(); lp.y = sy + dy.toInt(); wm.updateViewLayout(root, lp) }
                }
                MotionEvent.ACTION_UP -> if (!moved) tap()
            }
            true
        }
        wm.addView(root, lp)
    }

    private fun tap() {
        val n = Store.increment(this)
        monster.count = n
        Store.lineAt(n)?.let {
            speech.text = it
            handler.removeCallbacks(resetHint)
            handler.postDelayed(resetHint, 4000)
        }
    }

    override fun onDestroy() {
        root?.let { runCatching { wm.removeView(it) } }
        super.onDestroy()
    }
}
