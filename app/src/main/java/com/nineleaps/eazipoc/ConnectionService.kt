package com.nineleaps.eazipoc

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.nineleaps.eazipoc.Connection.ConnectionState
import com.nineleaps.eazipoc.Connection.LoggedInState
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import java.io.IOException


class ConnectionService : Service() {
    companion object {
        var sConnectionState: ConnectionState? = null
        var sLoggedInState: LoggedInState? = null
        const val UI_AUTHENTICATED = "UI Authenticated"
    }

    private val TAG = "ConnectionService"

    private var mActive: Boolean = false
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private var mConnection: Connection? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()");
    }

    private fun start() {
        Log.d(TAG, " Service Start() function called.")
        if (!mActive) {
            mActive = true
            if (mThread == null || !mThread!!.isAlive) {
                mThread = Thread(Runnable {
                    Looper.prepare()
                    mTHandler = Handler()
                    initConnection()
                    Looper.loop()
                })
                mThread!!.start()
            }
        }
    }

    private fun initConnection() {
        Log.d(TAG, "initConnection()")
        if (mConnection == null) {
            mConnection = Connection(this)
        }
        try {
            mConnection?.connect()

        } catch (e: IOException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
            //Stop the service all together.
            stopSelf()
        } catch (e: SmackException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
            stopSelf()
        } catch (e: XMPPException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
            stopSelf()
        }

    }

    private fun stop() {
        Log.d(TAG, "stop()")
        mActive = false
        mTHandler?.post {
            if (mConnection != null)
                mConnection?.disconnect()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        start()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
        stop()
    }

}