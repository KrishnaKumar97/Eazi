package com.nineleaps.eazipoc.utils

import android.app.Activity
import android.view.WindowManager

class Utils {
    companion object {
        /**
         * Function to make the activity appear in fullscreen
         * @param activity: COntext of activity to be made fullscreen
         */
        fun displayFullScreen(activity: Activity) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}