package com.rork.rockscout.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/** Walks up the [Context] wrapper chain to find the host [Activity]. */
fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
