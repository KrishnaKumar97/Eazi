package com.nineleaps.eazipoc.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.services.ChatService
import com.nineleaps.eazipoc.R
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class ChatActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    lateinit var groupId: String
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        sendButton = findViewById(R.id.button_chatbox_send)
        val intent = intent
        groupId = intent.getStringExtra("GROUP_ID")
        sendButton.setOnClickListener {
            try {
                val multiUserChatManager =
                    MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
                val muc =
                    multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal"))
//                Log.d("JIDDDDD", muc.toString()+"  "+ muc.isJoined)
//                muc.sendMessage("Hi all")
                val msg = Message(JidCreate.from("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal"), Message.Type.groupchat);
                msg.body = "message"
                muc.join(Resourcepart.from(ApplicationClass.connection.user.split("@")[0]))
                muc.sendMessage(msg)

//                val gson = Gson();
//                val json = sharedPreferences?.getString("muc", "");
//                val obj = gson.fromJson(json, MultiUserChat::class.java)
//                obj.sendMessage("HEllo")
            } catch (e: SmackException.NotConnectedException) {
                println(e.stackTrace)
            } catch (e: InterruptedException) {
                println(e.stackTrace)
            }catch (e:XMPPException){
                println(e.stackTrace)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ChatService::class.java)
        intent.putExtra("GROUP_ID_SERVICE", groupId)
        startService(intent)
    }

    private fun sendMessage(groupId: String) {
        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                sendMessageInBackground(groupId)
                Looper.loop()
            })
            mThread!!.start()
        }
    }

    private fun sendMessageInBackground(groupId: String) {
        try {
            val multiUserChatManager =
                MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
            val muc =
                multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("poloo@conference.ip-172-31-14-161.us-east-2.compute.internal"))
            muc.join(Resourcepart.from("Krishnaaaa"))
            muc.sendMessage("HIII")
            Log.d("ChatMessgae","SENT")
        } catch (e: SmackException.NotConnectedException) {
            println(e.stackTrace)
        } catch (e: InterruptedException) {
            println(e.stackTrace)
        }catch (e:XMPPException){
            println(e.stackTrace)
        }
    }

}
