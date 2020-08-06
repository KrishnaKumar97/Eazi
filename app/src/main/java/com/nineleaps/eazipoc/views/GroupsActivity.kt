package com.nineleaps.eazipoc.views

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.broadcastreceivers.GroupBroadcastReceiver
import com.nineleaps.eazipoc.services.GroupService
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.GroupListAdapter
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.models.GroupModel
import com.nineleaps.eazipoc.repositories.GroupRepository
import com.nineleaps.eazipoc.viewmodels.GroupViewModel
import kotlinx.android.synthetic.main.activity_groups.*

class GroupsActivity : AppCompatActivity(), GroupListAdapter.CellClickListener {


    private lateinit var createGroupButton: MaterialButton
    private var groupBroadcastReceiver: GroupBroadcastReceiver? = null
    private lateinit var groupViewModel: GroupViewModel
    private val groupList = ArrayList<GroupDatabaseModel>()
    private lateinit var recyclerViewForGroupList: RecyclerView
    private lateinit var noGroupsAvailable: ImageView
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null
    private lateinit var mProgressBar: ProgressBar
    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var refresh_group_button: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        if (!isNetworkConnected()) {
            Toast.makeText(
                this,
                "No internet. Groups may not be updated",
                Toast.LENGTH_LONG
            ).show()
        }
//        if (mThread == null || !mThread!!.isAlive) {
//            mThread = Thread(Runnable {
//                Looper.prepare()
//                mTHandler = Handler()
//                ApplicationClass.messageHistoryDatabase.clearAllTables()
//                Looper.loop()
//            })
//            mThread!!.start()
//        }
        Utils.displayFullScreen(this)
        initViews()
        initViewModel()
        observeData()
        groupBroadcastReceiver = GroupBroadcastReceiver()
        initClickListener()
    }

    override fun onStart() {
        super.onStart()
        startAllServices()
    }

    fun startAllServices() {
        startService(Intent(this, GroupService::class.java))
        groupBroadcastReceiver?.getGroups()?.let {
            GroupRepository.instance().addDataSource(it)
        }
        val filter = IntentFilter(GroupService.GROUP_FETCH)
        registerReceiver(groupBroadcastReceiver, filter)
    }

    fun stopAllServices() {
        stopService(Intent(this, GroupService::class.java))
        groupBroadcastReceiver?.getGroups()?.let { GroupRepository.instance().removeDataSource(it) }
        unregisterReceiver(groupBroadcastReceiver)
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this)
            .get(GroupViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()
        stopAllServices()
    }

    private fun initViews() {
        createGroupButton = findViewById(R.id.create_group_button)
        noGroupsAvailable = findViewById(R.id.empty_state_image_view)
        recyclerViewForGroupList = findViewById(R.id.recyclerViewGroup)
        refresh_group_button = findViewById(R.id.refresh_group_button)
        mProgressBar = findViewById(R.id.groups_progress_bar)
    }

    private fun observeData() {
        groupViewModel.getGroupListLiveData(ApplicationClass.connection.user.split("@")[0])
            .observe(this, Observer {
                if (!it.isNullOrEmpty()) {
                    groupList.clear()
                    recyclerViewForGroupList.visibility = View.VISIBLE
                    noGroupsAvailable.visibility = View.GONE
                    groupList.addAll(it)
                    initRecyclerView()
                    groupListAdapter?.notifyDataSetChanged()
                } else {
                    recyclerViewForGroupList.visibility = View.GONE
                    noGroupsAvailable.visibility = View.VISIBLE
                }
            })
    }

    private fun initClickListener() {
        createGroupButton.setOnClickListener {
            startActivity(Intent(this, GroupDetailsActivity::class.java))
        }

        refresh_group_button.setOnClickListener {
            stopAllServices()
            startAllServices()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewForGroupList.layoutManager = layoutManager as RecyclerView.LayoutManager?
        groupListAdapter =
            GroupListAdapter(
                groupList, this
            )
        recyclerViewForGroupList.adapter = groupListAdapter
    }

    override fun onCellClickListener(groupData: GroupDatabaseModel) {
        mProgressBar.visibility = View.VISIBLE
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("GROUP_ID", groupData.groupName)
        startActivity(intent)
        mProgressBar.visibility = View.GONE
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
