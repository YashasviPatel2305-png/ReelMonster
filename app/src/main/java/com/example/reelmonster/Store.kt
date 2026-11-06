package com.example.reelmonster

import android.content.Context
import java.time.LocalDate

/** Offline storage: one counter per day in SharedPreferences. */
object Store {
    const val SEC_PER_REEL = 25 // assumed time per reel (143 reels ~ 1 hour)

    private fun p(c: Context) = c.getSharedPreferences("reel_monster", Context.MODE_PRIVATE)
    private fun key(d: LocalDate) = "day_$d"

    fun today(c: Context): Int = p(c).getInt(key(LocalDate.now()), 0)

    fun increment(c: Context): Int {
        val n = today(c) + 1
        p(c).edit().putInt(key(LocalDate.now()), n).apply()
        return n
    }

    fun last7(c: Context): List<Pair<LocalDate, Int>> =
        (6 downTo 0).map { val d = LocalDate.now().minusDays(it.toLong()); d to p(c).getInt(key(d), 0) }

    var enabled: Boolean
        get() = false
        set(_) {}

    fun isEnabled(c: Context) = p(c).getBoolean("enabled", false)
    fun setEnabled(c: Context, v: Boolean) = p(c).edit().putBoolean("enabled", v).apply()

    fun timeLost(count: Int): String {
        val m = count * SEC_PER_REEL / 60
        return if (m >= 60) "${m / 60}h ${m % 60}m" else "${m}m"
    }

    private val milestones = listOf(
        10 to "Yum! Tasty scroll.",
        25 to "Burp. Your homework says hi.",
        50 to "I'm getting big...",
        100 to "100! Touch some grass?",
        150 to "I'm huge. Go outside!",
        200 to "I have become the reel."
    )

    /** Line shown only when the count exactly hits a milestone (null otherwise). */
    fun lineAt(n: Int): String? = milestones.firstOrNull { it.first == n }?.second

    /** Latest milestone line reached, for the dashboard. */
    fun lineFor(n: Int): String =
        milestones.lastOrNull { it.first <= n }?.second ?: "Feed me reels... or don't."
}
