package com.nineleaps.eazipoc.views

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.UserListAdapter
import com.nineleaps.eazipoc.broadcastreceivers.UserBroadcastReceiver
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.UserModel
import com.nineleaps.eazipoc.repositories.UserRepository
import com.nineleaps.eazipoc.services.UserService
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.viewmodels.GroupViewModel
import com.nineleaps.eazipoc.viewmodels.UserViewModel
import org.jivesoftware.smackx.muc.MultiUserChatManager
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
    private lateinit var noUsersAvailableImageView: ImageView
    private lateinit var refreshContacts: MaterialButton

    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private var groupName: String? = null

    /**
     * OnCreate overridden function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_contacts)

        // calling function to make the activity display in full screen
        Utils.displayFullScreen(this)
        val bundle = intent.extras
        groupName = bundle?.getString("group_name")
        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        initViews()
        initViewModel()
        initClickListener()
        observeData()
        userBroadcastReceiver = UserBroadcastReceiver()
    }

    /**
     * Function to initialize all the lateinit variables
     */
    private fun initViews() {
        submitButton = findViewById(R.id.submit_group_button)
        noUsersAvailableImageView = findViewById(R.id.empty_state_image_view)
        recyclerViewForUserList = findViewById(R.id.recyclerView)
        refreshContacts = findViewById(R.id.refresh_contacts_button)
    }

    /**
     * Function to initialize userViewModel and groupViewModel
     */
    private fun initViewModel() {
        userViewModel = ViewModelProviders.of(this)
            .get(UserViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this)
            .get(GroupViewModel::class.java)
    }

    /**
     * Function to initialize the click listeners of buttons present in the activity
     * On click of submit button, adds a group to the server and navigates to the GroupsActivity activity
     */
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

    /**
     * Function calls the method to add a group in the background
     * @param groupName: Name of the group to be added to the server
     * @param listOfJIDs: List of selected JIDs to be added to the group
     */
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

    /**
     * Function to create MultiUserChat and send invites to all the selected users
     * @param groupName: Name of the group
     * @param listOfJIDs: List of JIDs to be added to the group
     */
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

    /**
     * Function to observe changes in user list live data
     */
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

    /**
     * Function to compare the fetched JID list from the server with the contacts in the device
     * Function displays only contacts which are present in the server as well as in the device's contact list
     * @param jidList: Jid list fetched from the server
     */
    private fun compareNumbers(jidList: ArrayList<String>) {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val people = this.contentResolver.query(uri, projection, null, null, null)
        val indexName = people?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val indexNumber = people?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        var count = 0
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
            count++
        } while (people?.moveToNext()!!)
        if (userModelList.isEmpty()) {
            noUsersAvailableImageView.visibility = View.VISIBLE
            recyclerViewForUserList.visibility = View.GONE
        } else {
            initRecyclerView()
            noUsersAvailableImageView.visibility = View.GONE
            recyclerViewForUserList.visibility = View.VISIBLE
        }
    }

    /**
     * This function helps to initialize Recycler view
     */
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

    /**
     * onStart overridden function
     * Calls Function to start the user service and to register the userBroadcastReceiver
     */
    override fun onStart() {
        super.onStart()
        startService(Intent(this, UserService::class.java))
        userBroadcastReceiver?.getUsers()?.let {
            UserRepository.instance().addDataSource(it)
        }
        val filter = IntentFilter(UserService.USERS_FETCHED)
        registerReceiver(userBroadcastReceiver, filter)
    }

    /**
     * onStop overridden function
     * Calls Function to unregister the userBroadcastReceiver
     */
    override fun onStop() {
        super.onStop()
        userBroadcastReceiver?.getUsers()?.let { UserRepository.instance().removeDataSource(it) }
        unregisterReceiver(userBroadcastReceiver)
        finish()
    }

}
