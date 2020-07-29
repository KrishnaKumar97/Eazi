package com.nineleaps.eazipoc.views

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.nineleaps.Utils
import com.nineleaps.eazipoc.ConnectionService
import com.nineleaps.eazipoc.R

class SplashActivity : AppCompatActivity() {
    private lateinit var mProgressBar: ProgressBar
    private var sharedPreferences: SharedPreferences? = null
    private var isLoggedIn: Boolean? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Utils.displayFullScreen(this)
        init()
        navigateActivity()
    }

    private fun navigateActivity() {
        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences?.getBoolean("isLoggedIn", false)
        if (isLoggedIn!!) {
            startService(Intent(this, ConnectionService::class.java))
        } else {
            Handler().postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
            }, 3000)
        }

    }

    override fun onResume() {
        super.onResume()
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ConnectionService.UI_AUTHENTICATED -> {
                        mProgressBar.visibility = View.GONE
                        startActivity(Intent(this@SplashActivity, GroupsActivity::class.java))
                    }
                }
            }

        }
        val filter = IntentFilter(ConnectionService.UI_AUTHENTICATED)
        this.registerReceiver(mBroadcastReceiver, filter)
    }

    /**
     * initialize all lateinit variables
     */
    private fun init() {
        mProgressBar = findViewById(R.id.splash_screen_progress_bar)
    }

}
