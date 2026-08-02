package com.rork.rockscout.ui.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import java.util.concurrent.atomic.AtomicLong

/**
 * Project-wide debounce for back-button navigation. A single atomic is safe
 * because RockScout uses only one NavController at a time. Prevents rapid
 * successive back presses from calling [popBackStack] while a transition is
 * still in progress, which is a common source of IllegalStateException crashes.
 */
private val lastBackPressMs = AtomicLong(0L)
private const val BACK_DEBOUNCE_MS = 300L

/**
 * Pops the back stack only if the current entry is resumed and the debounce
 * window has passed. Returns `true` if a pop was attempted, `false` otherwise.
 * Any exception is swallowed so the app never crashes from a navigation race.
 */
fun NavController.safePopBackStack(): Boolean {
    val now = System.currentTimeMillis()
    val last = lastBackPressMs.get()
    if (now - last < BACK_DEBOUNCE_MS) return false
    if (!lastBackPressMs.compareAndSet(last, now)) return false

    return try {
        val entry = currentBackStackEntry
        val canPop = entry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true &&
            previousBackStackEntry != null
        if (canPop) {
            popBackStack()
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }
}
