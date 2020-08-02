package com.nineleaps.eazipoc.utils

import android.app.Activity
import android.view.WindowManager

class Utils {
    companion object {
        fun displayFullScreen(activity: Activity) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}