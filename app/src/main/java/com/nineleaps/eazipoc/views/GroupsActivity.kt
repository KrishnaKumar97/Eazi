package com.nineleaps.eazipoc.views

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

class GroupsActivity : AppCompatActivity(), GroupListAdapter.CellClickListener {


    private lateinit var createGroupButton: MaterialButton
    private var groupBroadcastReceiver: GroupBroadcastReceiver? = null
    private lateinit var groupViewModel: GroupViewModel
    private val groupList = ArrayList<GroupModel>()
    private lateinit var recyclerViewForGroupList: RecyclerView
    private lateinit var noGroupsAvailable: ImageView
    private var groupListAdapter: GroupListAdapter? = null

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
        startService(Intent(this, GroupService::class.java))
        groupBroadcastReceiver?.getGroups()?.let {
            GroupRepository.instance().addDataSource(it)
        }
        val filter = IntentFilter(GroupService.GROUP_FETCH)
        registerReceiver(groupBroadcastReceiver, filter)
    }

    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this)
            .get(GroupViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()
        groupBroadcastReceiver?.getGroups()?.let { GroupRepository.instance().removeDataSource(it) }
        unregisterReceiver(groupBroadcastReceiver)
    }

    private fun initViews() {
        createGroupButton = findViewById(R.id.create_group_button)
        noGroupsAvailable = findViewById(R.id.empty_state_image_view)
        recyclerViewForGroupList = findViewById(R.id.recyclerViewGroup)

    }

    private fun observeData() {
        groupViewModel.getData()?.observe(this, Observer {
            if (it != null) {
                groupList.clear()
                for (group in it) {
                    groupList.add(group)
                }
                initRecyclerView()
                groupListAdapter?.notifyDataSetChanged()
            }
        })
//        Log.d("GROUPLIVEDATA1", groupList.toString())
//        if (groupList.isEmpty()) {
//            Log.d("GROUPLIVEDATA2", groupList.toString())
//
//            noGroupsAvailable.visibility = View.VISIBLE
//            recyclerViewForGroupList.visibility = View.GONE
//        } else {
//            Log.d("GROUPLIVEDATA3", groupList.toString())
//
//            initRecyclerView()
//            noGroupsAvailable.visibility = View.GONE
//            recyclerViewForGroupList.visibility = View.VISIBLE
//        }
    }

    private fun initClickListener() {
        createGroupButton.setOnClickListener {
            startActivity(Intent(this, GroupDetailsActivity::class.java))
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
        Toast.makeText(this,"$groupData",Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("GROUP_ID", groupData.groupName)
        startActivity(intent)
    }

}
