package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global red-light / night-vision mode toggle for UV collecting.
 *
 * When enabled, a translucent red overlay covers the entire app so the
 * user's dark-adapted vision is preserved while using UV lights in the field.
 * The toggle is persisted via [PersistenceManager] and observable via [enabled].
 */
object NightModeManager {

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    fun initialize() {
        _enabled.value = PersistenceManager.isNightModeEnabled()
    }

    fun toggle(): Boolean {
        val newValue = !_enabled.value
        setEnabled(newValue)
        return newValue
    }

    fun setEnabled(value: Boolean) {
        _enabled.value = value
        PersistenceManager.saveNightModeEnabled(value)
    }
}
