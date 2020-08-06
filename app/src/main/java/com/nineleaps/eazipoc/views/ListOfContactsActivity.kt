package com.nineleaps.eazipoc.views

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.broadcastreceivers.UserBroadcastReceiver
import com.nineleaps.eazipoc.services.UserService
import com.nineleaps.eazipoc.adapters.UserListAdapter
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.UserModel
import com.nineleaps.eazipoc.repositories.UserRepository
import com.nineleaps.eazipoc.viewmodels.GroupViewModel
import com.nineleaps.eazipoc.viewmodels.UserViewModel
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.xdata.Form
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class ListOfContactsActivity : AppCompatActivity() {
    private var userBroadcastReceiver: UserBroadcastReceiver? = null
    private lateinit var userViewModel: UserViewModel
    private val userModelList = ArrayList<UserModel>()
    private val jidList = ArrayList<String>()
    private val finalNumberList = ArrayList<String>()
    private lateinit var groupViewModel: GroupViewModel
    private var sharedPreferences: SharedPreferences? = null
    private var userListAdapter: UserListAdapter? = null
    private lateinit var recyclerViewForUserList: RecyclerView
    private lateinit var submitButton: MaterialButton
    private val selectedNumberList = ArrayList<String>()
    private lateinit var noUsersAvailable: ImageView
    private lateinit var refreshContacts: MaterialButton

    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private var groupName: String? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.displayFullScreen(this)
        setContentView(R.layout.activity_list_of_contacts)
        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        initViews()
        initViewModel()
        val bundle = intent.extras
        groupName = bundle?.getString("group_name")

        observeData()
        userBroadcastReceiver = UserBroadcastReceiver()
        initClickListener()
    }


    private fun initViews() {
        submitButton = findViewById(R.id.submit_group_button)
        noUsersAvailable = findViewById(R.id.empty_state_image_view)
        recyclerViewForUserList = findViewById(R.id.recyclerView)
        refreshContacts = findViewById(R.id.refresh_contacts_button)
    }

    private fun initClickListener() {
        submitButton.setOnClickListener {
            if (groupName != null) {
                addGroup(groupName!!, selectedNumberList)
                Toast.makeText(this, "Group Created Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, GroupsActivity::class.java))
            }
        }

        refreshContacts.setOnClickListener {
            observeData()
        }
    }


    private fun initViewModel() {
        userViewModel = ViewModelProviders.of(this)
            .get(UserViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this)
            .get(GroupViewModel::class.java)
    }

    private fun observeData() {
        userViewModel.getData()?.observe(this, Observer {
            if (it != null) {
                for (user in it) {
                    jidList.add(user.jid.toString().replace("[", "").replace("]", ""))
                }
                if (!jidList.isNullOrEmpty()) {
                    compareNumbers(jidList)
                }
            }
        })
    }

    private fun addGroup(groupName: String, listOfJIDs: ArrayList<String>) {
        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                addGroupInBackground(groupName, listOfJIDs)
                Looper.loop()
            })
            mThread!!.start()
        }
    }


    private fun addGroupInBackground(groupName: String, listOfJIDs: ArrayList<String>) {

        try {
            MultiUserChatManager.getInstanceFor(ApplicationClass.connection)

            val multiUserChatManager =
                MultiUserChatManager.getInstanceFor(ApplicationClass.connection)
            val jid =
                JidCreate.entityBareFrom(groupName + "@conference.ip-172-31-14-161.us-east-2.compute.internal")
            val muc = multiUserChatManager.getMultiUserChat(jid)
            muc.create(Resourcepart.from(ApplicationClass.connection.user.split("@")[0]))
            val form = muc.configurationForm
            val answerForm = form.createAnswerForm()
            answerForm.setAnswer("muc#roomconfig_persistentroom", true)
            muc.sendConfigurationForm(answerForm)
            groupViewModel.storeGroupInDB(
                GroupDatabaseModel(
                    groupName = groupName,
                    userNickName = ApplicationClass.connection.user.split("@")[0]
                )
            )
            for (item in listOfJIDs) {
                muc.invite(
                    JidCreate.entityBareFrom("$item@ip-172-31-14-161.us-east-2.compute.internal"),
                    "Meet me in this excellent room"
                );
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }


    }


    private fun compareNumbers(jidList: ArrayList<String>) {

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val people = this.contentResolver.query(uri, projection, null, null, null)

        val indexName = people?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val indexNumber = people?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        var j_count = 0
        var number: String
        people?.moveToFirst()
        do {

            val name = indexName?.let { people.getString(it) }
            number = indexNumber?.let { people.getString(it) }.toString().replace("+91", "")
                .replace(" ", "").replace("-", "")
            if (jidList.contains(number) && !finalNumberList.contains(number)) {
                userModelList.add(UserModel(number, name))
                finalNumberList.add(number)
                userListAdapter?.notifyDataSetChanged()
            }
            j_count++
        } while (people?.moveToNext()!!)
        if (userModelList.isEmpty()) {
            noUsersAvailable.visibility = View.VISIBLE
            recyclerViewForUserList.visibility = View.GONE
        } else {
            initRecyclerView()
            noUsersAvailable.visibility = View.GONE
            recyclerViewForUserList.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        startService(Intent(this, UserService::class.java))
        userBroadcastReceiver?.getUsers()?.let {
            UserRepository.instance().addDataSource(it)
        }
        val filter = IntentFilter(UserService.USERS_FETCHED)
        registerReceiver(userBroadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        userBroadcastReceiver?.getUsers()?.let { UserRepository.instance().removeDataSource(it) }
        unregisterReceiver(userBroadcastReceiver)
        finish()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewForUserList.layoutManager = layoutManager
        userListAdapter =
            UserListAdapter(
                userModelList,
                finalNumberList,
                object : UserListAdapter.CheckBoxClickListener {
                    override fun onClick(numberList: ArrayList<String>) {
                        selectedNumberList.clear()
                        selectedNumberList.addAll(numberList)
                    }

                })
        recyclerViewForUserList.adapter = userListAdapter
    }
}
