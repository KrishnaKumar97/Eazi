package com.nineleaps.eazipoc.views

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.MessageListAdapter
import com.nineleaps.eazipoc.models.MessageModel
import com.nineleaps.eazipoc.utils.Utils
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MucEnterConfiguration
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class ChatActivity : AppCompatActivity(), MessageListener {
    private lateinit var sendButton: Button
    private lateinit var editText: EditText
    lateinit var groupId: String
    private lateinit var multiUserChatManager: MultiUserChatManager
    private lateinit var mucEnterConfiguration: MucEnterConfiguration
    private lateinit var muc: MultiUserChat
    private val messageList = ArrayList<MessageModel>()
    private lateinit var recyclerViewForMessageList: RecyclerView
    private var messageListAdapter: MessageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        Utils.displayFullScreen(this)
        val intent = intent
        groupId = intent.getStringExtra("GROUP_ID")
        initViews()
        initClickListeners()
        initMuc()
        initRecyclerView()
    }

    private fun initViews() {
        sendButton = findViewById(R.id.button_chatbox_send)
        recyclerViewForMessageList = findViewById(R.id.recyclerViewChat)
        editText = findViewById(R.id.edittext_chatbox)
    }

    private fun initClickListeners() {
        sendButton.setOnClickListener {
            try {
                val msg = Message(
                    JidCreate.from("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal"),
                    Message.Type.groupchat
                )
                if (editText.text.isNotEmpty()) {
                    msg.body = editText.text.toString()
                    muc.sendMessage(msg)
                    editText.text.clear()
                }
            } catch (e: SmackException.NotConnectedException) {
                println(e.stackTrace)
            } catch (e: InterruptedException) {
                println(e.stackTrace)
            } catch (e: XMPPException) {
                println(e.stackTrace)
            }
        }
    }

    private fun initMuc() {
        multiUserChatManager = MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
        muc =
            multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal"))
        mucEnterConfiguration = muc.getEnterConfigurationBuilder(
            Resourcepart.from(ApplicationClass.connection.user.split("@")[0])
        )!!
            .requestNoHistory()
            .build()
        if (!muc.isJoined)
            muc.join(mucEnterConfiguration)
        muc.addMessageListener(this)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewForMessageList.layoutManager = layoutManager as RecyclerView.LayoutManager?
        messageListAdapter =
            MessageListAdapter(
                messageList
            )
        recyclerViewForMessageList.adapter = messageListAdapter
    }

    override fun onStart() {
        super.onStart()
        messageList.clear()
    }

    override fun onStop() {
        super.onStop()
        muc.removeMessageListener(this)
    }

    override fun processMessage(message: Message?) {
        if (message?.from.toString().contains("/")) {
            messageList.add(MessageModel(message?.from.toString().split("/")[1], message?.body))
            runOnUiThread {
                messageListAdapter?.notifyDataSetChanged()
            }
        }
    }

}
