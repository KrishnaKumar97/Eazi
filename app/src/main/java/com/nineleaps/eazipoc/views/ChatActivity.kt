package com.nineleaps.eazipoc.views

import android.database.Observable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.intentservice.chatui.models.ChatMessage
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.MessageListAdapter
import com.nineleaps.eazipoc.models.MessageDatabaseModel
import com.nineleaps.eazipoc.models.MessageModel
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import org.jivesoftware.smackx.mam.element.MamElements
import org.jivesoftware.smackx.muc.MucEnterConfiguration
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.rsm.packet.RSMSet
import org.jivesoftware.smackx.xdata.FormField
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.EntityJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.util.XmppStringUtils

class ChatActivity : AppCompatActivity(), MessageListener {
    private lateinit var sendButton: Button
    private lateinit var editText: EditText
    lateinit var groupId: String
    private lateinit var multiUserChatManager: MultiUserChatManager
    private lateinit var mucEnterConfiguration: MucEnterConfiguration
    private lateinit var mamManager: MamManager
    private lateinit var muc: MultiUserChat
    private lateinit var mucJID: EntityJid
    private val messageList = ArrayList<MessageModel>()
    private lateinit var recyclerViewForMessageList: RecyclerView
    private var messageListAdapter: MessageListAdapter? = null
    private var mThread: Thread? = null
    private var mThreadFetch: Thread? = null
    private lateinit var fetchDataFromDB: List<MessageDatabaseModel>
    private var mTHandler: Handler? = null
    private var mTHandlerFetch: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
//        Utils.displayFullScreen(this)
        val intent = intent
        receiveDatabaseData()
        groupId = intent.getStringExtra("GROUP_ID")
        initViews()
        initClickListeners()
        initMuc()
        initRecyclerView()
    }

    private fun receiveDatabaseData() {
        if (mThreadFetch == null || !mThreadFetch!!.isAlive) {
            mThreadFetch = Thread(Runnable {
                Looper.prepare()
                mTHandlerFetch = Handler()
                fetchDataFromDB =
                    ApplicationClass.messageHistoryDatabase.messageHistoryDAO.fetchAllMessage(
                        groupId
                    )
                val tempList = ArrayList<MessageModel>()
                for (item in fetchDataFromDB) {
                    tempList.add(MessageModel(item.userNickName, item.messageBody))
                }
                messageList.clear()
                messageList.addAll(tempList)
                runOnUiThread {
                    messageListAdapter?.notifyDataSetChanged()
                }
                Looper.loop()
            })
            mThreadFetch!!.start()
        }
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
                    pushMessageToDB(
                        ApplicationClass.connection.user.split("@")[0],
                        editText.text.toString()
                    )
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

    private fun storeMessageInDB(userNickName: String, messageBody: String) {
        ApplicationClass.messageHistoryDatabase.messageHistoryDAO.insertMessage(
            MessageDatabaseModel(
                groupName = groupId,
                userNickName = userNickName,
                messageBody = messageBody
            )
        )
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
            pushMessageToDB(
                message?.from.toString().split("/")[1],
                message?.body.toString()
            )
            runOnUiThread {
                messageListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun pushMessageToDB(userNickName: String, messageBody: String) {

        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                storeMessageInDB(userNickName, messageBody)
                Looper.loop()
            })
            mThread!!.start()
        }

        // check the connection object
        //get the instance of MamManager
//        mamManager = MamManager.getInstanceFor(muc)
        //enable it for fetching messages
//            mamManager.enableMamForAllMessages()

//            val mQuery = mamManager.queryMostRecentPage(mucJID,20)
//            Log.d("MAMQUERY",mQuery.messages.toString())
        // Function for fetching messages
//            disposableMessages = getObservableMessages()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ listOfMessages ->
//                    tempMessageList = listOfMessages
//                }, { t ->
//                    Log.e(LOG_TAG, "-> initMam -> onError ->", t)
//                }, {
//                    messageList.value = tempMessageList
//                    tempList.clear()
//                })
//        }
//    }

//        val mamQueryResult = mamManager.queryMostRecentPage(mucJID, 20)
//        Log.d("MAMQUERY", mamQueryResult.toString())


    }

    private fun fetchMAM() {
        mamManager = MamManager.getInstanceFor(ApplicationClass.connection, mucJID)
        val mamQueryResult = mamManager.queryMostRecentPage(mucJID, 20)
//        val form = DataForm(DataForm.Type.submit)
//        val field = FormField(FormField.FORM_TYPE)
//        field.setType(FormField.Type.hidden);
//        field.addValue(MamElements.NAMESPACE);
//        form.addField(field);
//
//        val formField = FormField("with")
//        formField.addValue(mucJID)
//        form.addField(formField)
//
//        val rsmSet = RSMSet(20, "", RSMSet.PageDirection.before)
//        val mamQueryResult = mamManager.page(form, rsmSet)
        Log.d("MAMQUERY", mamQueryResult.messages.toString())

//        val mamQueryResult = mamManager.queryMostRecentPage(mucJID, 20)
    }

}
