package com.nineleaps.eazipoc.views

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.services.ConnectionService
import com.nineleaps.eazipoc.utils.Utils

class SplashActivity : AppCompatActivity() {
    private lateinit var mProgressBar: ProgressBar
    private var sharedPreferences: SharedPreferences? = null
    private var isLoggedIn: Boolean? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null

    /**
     * OnCreate overridden function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // calling function to make the activity display in full screen
        Utils.displayFullScreen(this)
        init()
        navigateActivity()
    }

    /**
     * function to initialize all the lateinit variables
     */
    private fun init() {
        mProgressBar = findViewById(R.id.splash_screen_progress_bar)
    }

    /**
     * Function to check if the user is opening the app for the first time
     * If first time, navigates to LoginActivity
     * Else, starts ConnectionService to connect to the server and login automatically
     */
    private fun navigateActivity() {
        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences?.getBoolean("isLoggedIn", false)
        if (isLoggedIn!!) {
            startService(Intent(this, ConnectionService::class.java))
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 2000)
        }
    }

    /**
     * onResume overridden function
     * If the user is authenticated, navigates to the GroupsActivity
     */
    override fun onResume() {
        super.onResume()
        mBroadcastReceiver = object : BroadcastReceiver() {
            // Executes when broadcast message is received from the ConnectionService
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ConnectionService.UI_AUTHENTICATED -> {
                        mProgressBar.visibility = View.GONE
                        startActivity(Intent(this@SplashActivity, GroupsActivity::class.java))
                        finish()
                    }
                }
            }

        }
        val filter = IntentFilter(ConnectionService.UI_AUTHENTICATED)
        this.registerReceiver(mBroadcastReceiver, filter)
    }

    /**
     * onPause overridden function
     */
    override fun onPause() {
        super.onPause()
        if (mBroadcastReceiver != null)
            this.unregisterReceiver(mBroadcastReceiver)
    }

}
