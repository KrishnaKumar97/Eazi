package com.nineleaps.eazipoc

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.InvitationListener
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.packet.MUCUser
import org.jxmpp.jid.EntityJid
import org.jxmpp.jid.parts.Resourcepart


class GroupService : Service(), InvitationListener {
    companion object {
        const val GROUP_INVITATION = "Group Invite"
    }

    private val TAG = "GroupService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()");
    }

    private fun start() {
        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                fetchGroups()
                Looper.loop()
            })
            mThread!!.start()
        }
    }

    private fun fetchGroups() {
        MultiUserChatManager.getInstanceFor(ApplicationClass.connection).addInvitationListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        start()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun invitationReceived(
        conn: XMPPConnection?,
        room: MultiUserChat?,
        inviter: EntityJid?,
        reason: String?,
        password: String?,
        message: Message?,
        invitation: MUCUser.Invite?
    ) {
        Log.d(TAG, "Invitation Received")
        room?.join(Resourcepart.from(ApplicationClass.connection.user.toString()))
    }
}