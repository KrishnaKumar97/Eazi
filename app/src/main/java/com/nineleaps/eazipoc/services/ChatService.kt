package com.nineleaps.eazipoc.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.nineleaps.eazipoc.ApplicationClass
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate


class ChatService : Service(), MessageListener {
    private val TAG = "ChatService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private lateinit var intentReceived: Intent
    private var groupId: String? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()");
    }

    fun start() {
        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                fetchMessages()
                Looper.loop()
            })
            mThread!!.start()
        }
    }

    private fun fetchMessages() {
        Log.d(TAG, "fetchMessagescalled")
        try {
            val multiUserChatManager =
                MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
            val muc =
                multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$groupId@conference.localhost"))
            muc.addMessageListener(this)
        } catch (e: Exception) {
            println(e.stackTrace)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        if (intent != null) {
            intentReceived = intent
        }
        groupId = intent?.extras?.getString("GROUP_ID_SERVICE")
        Log.d(TAG,groupId)
        start()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun processMessage(message: Message?) {
        Log.d(TAG, "MessageReceived")
    }
}