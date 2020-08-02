package com.nineleaps.eazipoc.services

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.MessageModel
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate


class ChatService : Service(), MessageListener {
    private val TAG = "ChatService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private lateinit var intentReceived: Intent
    lateinit var activity: Callbacks
    val mBinder = LocalBinder()

    private var groupId: String? = null
    private var listOfMessage = ArrayList<MessageModel>()
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        fun getServiceInstance(): ChatService {
            return this@ChatService
        }
    }

    fun registerClient(activity: Activity) {
        this.activity = activity as Callbacks
    }
//    companion object {
//        const val NEW_MESSAGE = "New Message"
//    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()");
    }

    fun start() {
        listOfMessage.clear()
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
                multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal"))
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
        start()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun processMessage(message: Message?) {
//        listOfMessage.add(MessageModel(message?.from.toString(), message?.body))
//        val intent = Intent(NEW_MESSAGE);
//        intent.setPackage(applicationContext.packageName);
//        intent.putParcelableArrayListExtra("fetched_messages", listOfMessage)
//        applicationContext.sendBroadcast(intent)
        Log.d(TAG, "MessageReceived")
        activity.updateClient(MessageModel(message?.from.toString(), message?.body))
    }

    interface Callbacks {
        fun updateClient(messageModel: MessageModel?)
    }

}