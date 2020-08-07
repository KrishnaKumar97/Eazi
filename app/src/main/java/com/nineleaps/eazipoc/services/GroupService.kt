package com.nineleaps.eazipoc.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.viewmodels.GroupViewModel
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
import org.jxmpp.jid.parts.Resourcepart

/**
 * Service which is responsible to listen for incoming group invites
 */
class GroupService : Service(), InvitationListener {
    private val TAG = "GroupService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private var flag: Boolean = false


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    /**
     * Function to invoke method which initializes the invitation listener in the background
     */
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

    /**
     * Function initialized the Invitation Listener
     */
    private fun fetchGroups() {
        Log.d(TAG, "FetchGroupscalled")
        try {
            val multiUserChatManager =
                MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
            multiUserChatManager.addInvitationListener(this)
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

    /**
     * Method is invoked when an invite is received to join a Multi User Chat room
     */
    override fun invitationReceived(
        conn: XMPPConnection?,
        room: MultiUserChat?,
        inviter: EntityJid?,
        reason: String?,
        password: String?,
        message: Message?,
        invitation: MUCUser.Invite?
    ) {
        flag = true
        if (flag) {
            flag = false
            try {
                room?.join(Resourcepart.from(ApplicationClass.connection.user.split("@")[0]))
                val viewModel = GroupViewModel()
                viewModel.storeGroupInDB(
                    GroupDatabaseModel(
                        groupName = room.toString().split("@")[0].split(" ")[1],
                        userNickName = ApplicationClass.connection.user.split("@")[0]
                    )
                )
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
}