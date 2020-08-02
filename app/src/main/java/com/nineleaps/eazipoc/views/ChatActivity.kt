package com.nineleaps.eazipoc.views

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.MessageListAdapter
import com.nineleaps.eazipoc.models.MessageModel
import com.nineleaps.eazipoc.services.ChatService
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class ChatActivity : AppCompatActivity(), ChatService.Callbacks {
    private lateinit var sendButton: Button
    lateinit var groupId: String
    lateinit var chatService: ChatService
    var serviceIntent: Intent? = null
    private val messageList = ArrayList<MessageModel>()
    private lateinit var recyclerViewForMessageList: RecyclerView
    private var messageListAdapter: MessageListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent = intent
        groupId = intent.getStringExtra("GROUP_ID")
        serviceIntent = Intent(this, ChatService::class.java)

        initViews()
        initRecyclerView()

//        initViewModel()
//        observeData()
        initClickListener()
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: ChatService.LocalBinder = service as ChatService.LocalBinder
            chatService = binder.getServiceInstance()
            chatService.registerClient(this@ChatActivity)
        }

    }

    private fun initViews() {
        sendButton = findViewById(R.id.button_chatbox_send)
        recyclerViewForMessageList = findViewById(R.id.recyclerViewChat)
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

    private fun initClickListener() {
        sendButton.setOnClickListener {
            try {
                val multiUserChatManager =
                    MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
                val muc =
                    multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom("$groupId@conference.localhost"))
                val msg = Message(
                    JidCreate.from("$groupId@conference.localhost"),
                    Message.Type.groupchat
                );
                msg.body = "message"
                muc.join(Resourcepart.from(ApplicationClass.connection.user.split("@")[0]))
                muc.sendMessage(msg)
            } catch (e: SmackException.NotConnectedException) {
                println(e.stackTrace)
            } catch (e: InterruptedException) {
                println(e.stackTrace)
            } catch (e: XMPPException) {
                println(e.stackTrace)
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        val intent = Intent(this, ChatService::class.java)
        serviceIntent?.putExtra("GROUP_ID_SERVICE", groupId)
        startService(serviceIntent)
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
//        messageBroadcastReceiver?.getMessages()?.let {
//            MessageRepository.instance().addDataSource(it)
//        }
//        val filter = IntentFilter(ChatService.NEW_MESSAGE)
//        registerReceiver(messageBroadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unbindService(mConnection);
        stopService(serviceIntent);
//        messageBroadcastReceiver?.getMessages()
//            ?.let { MessageRepository.instance().removeDataSource(it) }
//        unregisterReceiver(messageBroadcastReceiver)
    }

    override fun updateClient(messageModel: MessageModel?) {
        if (messageModel != null) {
            messageList.add(messageModel)
        }
        Log.d("MessageReceivedActivity", messageModel.toString())
    }
}
