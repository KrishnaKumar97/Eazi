package com.nineleaps.eazipoc.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.GroupModel
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.InvitationListener
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatException
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muc.packet.MUCUser
import org.jxmpp.jid.EntityJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart


class GroupService : Service(), InvitationListener {

    companion object {
        const val GROUP_FETCH = "Group Fetch"
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

    fun start() {
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
        Log.d(TAG, "FetchGroupscalled")
        val groupList = ArrayList<GroupModel>()
        try {
            val multiUserChatManager =
                MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
            multiUserChatManager.addInvitationListener(this)
            try {
                val rooms =
                    multiUserChatManager.getHostedRooms(JidCreate.domainBareFrom("@conference.ip-172-31-14-161.us-east-2.compute.internal"))
                for (room in rooms) {
                    val group = GroupModel()
                    group.groupName = room.jid.localpart.toString()
                    groupList.add(group)
                }
            } catch (e: SmackException.NoResponseException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: XMPPException.XMPPErrorException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: SmackException.NotConnectedException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: MultiUserChatException.NotAMucServiceException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }

            val i = Intent(GROUP_FETCH)
            i.setPackage(applicationContext.packageName)
            i.putParcelableArrayListExtra("fetched_groups", groupList)
            applicationContext.sendBroadcast(i)
        } catch (e: Exception) {
            println(e.stackTrace)
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
        try {
            room?.join(Resourcepart.from(ApplicationClass.connection.user.split("@")[0]))
        } catch (e: SmackException.NoResponseException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: XMPPException.XMPPErrorException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: SmackException.NotConnectedException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: InterruptedException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: MultiUserChatException.NotAMucServiceException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}