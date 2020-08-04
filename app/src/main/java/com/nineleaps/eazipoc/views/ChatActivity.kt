package com.nineleaps.eazipoc.views

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.MessageListAdapter
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.models.MessageModel
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.viewmodels.MessageHistoryViewModel
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.muc.MucEnterConfiguration
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatException
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.EntityJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class ChatActivity : AppCompatActivity(), MessageListener {
    private lateinit var sendButton: Button
    private lateinit var editText: EditText
    private lateinit var groupId: String
    private lateinit var heading: TextView
    private lateinit var messageHistoryViewModel: MessageHistoryViewModel
    private lateinit var multiUserChatManager: MultiUserChatManager
    private lateinit var mucEnterConfiguration: MucEnterConfiguration
    private lateinit var mamManager: MamManager
    private lateinit var muc: MultiUserChat
    private lateinit var mucJID: EntityJid
    private val messageList = ArrayList<MessageModel>()
    private lateinit var recyclerViewForMessageList: RecyclerView
    private var messageListAdapter: MessageListAdapter? = null
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
//        Utils.displayFullScreen(this)
        val intent = intent
        groupId = intent.getStringExtra("GROUP_ID")
        initViews()
        initViewModel()
        if (isNetworkConnected()) {
            initMuc()
            fetchArchiveMessage()
        } else {
            Toast.makeText(
                this,
                "No internet. Showing cached data from the database",
                Toast.LENGTH_LONG
            ).show()
        }
        initClickListeners()
        observeData()
    }

    private fun initViewModel() {
        messageHistoryViewModel =
            ViewModelProviders.of(this).get(MessageHistoryViewModel::class.java)
    }

    private fun observeData() {
        messageHistoryViewModel.getMessageHistoryLiveData(groupId).observe(this, Observer {
            if (it != null) {
                messageList.clear()
                it.forEach {
                    messageList.add(MessageModel(it.userNickName, it.messageBody))
                }
                initRecyclerView()
            }
        })
    }


    private fun initViews() {
        sendButton = findViewById(R.id.button_chatbox_send)
        recyclerViewForMessageList = findViewById(R.id.recyclerViewChat)
        editText = findViewById(R.id.edittext_chatbox)
        heading = findViewById(R.id.group_heading)
        heading.text = groupId
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
        mucJID =
            JidCreate.entityBareFrom("$groupId@conference.ip-172-31-14-161.us-east-2.compute.internal")
        muc =
            multiUserChatManager.getMultiUserChat(mucJID as EntityBareJid?)
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
        layoutManager.stackFromEnd = true;

        messageListAdapter =
            MessageListAdapter(
                messageList
            )
        recyclerViewForMessageList.adapter = messageListAdapter
    }

    override fun onStop() {
        super.onStop()
        if (isNetworkConnected())
            muc.removeMessageListener(this)
    }

    override fun processMessage(message: Message?) {
        if (message?.from.toString().contains("/")) {
            messageList.add(MessageModel(message?.from.toString().split("/")[1], message?.body))
            messageHistoryViewModel.storeMessageInDB(
                MessageDatabaseModel(
                    groupName = groupId,
                    userNickName = message?.from.toString().split("/")[1],
                    messageBody = message?.body
                )
            )
            runOnUiThread {
                messageListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun fetchArchiveMessage() {

        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                fetchMAM()
                Looper.loop()
            })
            mThread!!.start()
        }
    }

    private fun fetchMAM() {
        try {
            val listOfMessages = ArrayList<MessageDatabaseModel>()
            val mamQueryArgs = MamManager.MamQueryArgs.builder()
                .queryLastPage()
                .build()

            mamManager = MamManager.getInstanceFor(muc)
            val mamQuery = mamManager.queryArchive(mamQueryArgs)
            val mamQueryCount =
                mamQuery.messageCount
            var i = 0
            while (i < mamQueryCount) {
                val mamQueryMessage =
                    mamQuery.mamResultExtensions[i].forwarded.forwardedStanza as Message
                listOfMessages.add(
                    MessageDatabaseModel(
                        groupName = groupId,
                        userNickName = mamQueryMessage.from.toString().split("/")[1],
                        messageBody = mamQueryMessage.body
                    )
                )
                i += 1
            }
            messageHistoryViewModel.storeMessageListInDB(groupId, listOfMessages)
        }catch (e: SmackException.NoResponseException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: XMPPException.XMPPErrorException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: SmackException.NotConnectedException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: InterruptedException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: SmackException.NotLoggedInException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
