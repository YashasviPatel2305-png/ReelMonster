package com.example.reelmonster

import android.content.*
import android.provider.Settings

/** Restarts the bubble after the phone reboots (if it was on). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context, i: Intent) {
        if (i.action == Intent.ACTION_BOOT_COMPLETED && Store.isEnabled(c) && Settings.canDrawOverlays(c))
            c.startForegroundService(Intent(c, BubbleService::class.java))
    }
}
