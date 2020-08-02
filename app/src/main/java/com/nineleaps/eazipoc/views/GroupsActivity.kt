package com.nineleaps.eazipoc.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.broadcastreceivers.GroupBroadcastReceiver
import com.nineleaps.eazipoc.services.GroupService
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.GroupListAdapter
import com.nineleaps.eazipoc.models.GroupModel
import com.nineleaps.eazipoc.repositories.GroupRepository
import com.nineleaps.eazipoc.viewmodels.GroupViewModel
import kotlinx.android.synthetic.main.activity_groups.*

class GroupsActivity : AppCompatActivity(), GroupListAdapter.CellClickListener {


    private lateinit var createGroupButton: MaterialButton
    private var groupBroadcastReceiver: GroupBroadcastReceiver? = null
    private lateinit var groupViewModel: GroupViewModel
    private val groupList = ArrayList<GroupModel>()
    private lateinit var recyclerViewForGroupList: RecyclerView
    private lateinit var noGroupsAvailable: ImageView
    private var groupListAdapter: GroupListAdapter? = null
    private lateinit var refresh_group_button: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
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
        finish()
    }

    private fun initViews() {
        createGroupButton = findViewById(R.id.create_group_button)
        noGroupsAvailable = findViewById(R.id.empty_state_image_view)
        recyclerViewForGroupList = findViewById(R.id.recyclerViewGroup)
        refresh_group_button = findViewById(R.id.refresh_group_button)

    }

    private fun observeData() {
        groupViewModel.getData()?.observe(this, Observer {
            if (it != null) {
                groupList.clear()
                Log.e("data", it.toString())
                for (group in it) {
                    groupList.add(group)
                }
                if (groupList.size == 0) {
                    noGroupsAvailable.visibility = View.VISIBLE

                } else {
                    noGroupsAvailable.visibility = View.GONE
                }
                initRecyclerView()
                groupListAdapter?.notifyDataSetChanged()
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

    override fun onCellClickListener(groupData: GroupModel) {
        Toast.makeText(this, "$groupData", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("GROUP_ID", groupData.groupName)
        startActivity(intent)
    }
}
