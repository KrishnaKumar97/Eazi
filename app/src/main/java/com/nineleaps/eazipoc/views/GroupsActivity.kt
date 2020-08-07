package com.nineleaps.eazipoc.views

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
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
import com.nineleaps.eazipoc.R
import com.nineleaps.eazipoc.adapters.GroupListAdapter
import com.nineleaps.eazipoc.models.GroupDatabaseModel
import com.nineleaps.eazipoc.services.GroupService
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.viewmodels.GroupViewModel

class GroupsActivity : AppCompatActivity(), GroupListAdapter.CellClickListener {

    private lateinit var createGroupButton: MaterialButton
    private lateinit var groupViewModel: GroupViewModel
    private val groupList = ArrayList<GroupDatabaseModel>()
    private lateinit var recyclerViewForGroupList: RecyclerView
    private lateinit var noGroupsAvailableImageView: ImageView
    private lateinit var mProgressBar: ProgressBar
    private var groupListAdapter: GroupListAdapter? = null

    /**
     * OnCreate overridden function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        // Calls function to check if network is available
        if (!isNetworkConnected()) {
            Toast.makeText(
                this,
                "No internet. Groups may not be updated",
                Toast.LENGTH_LONG
            ).show()
        }

        // calling function to make the activity display in full screen
        Utils.displayFullScreen(this)
        initViews()
        initViewModel()
        observeData()
        initClickListener()
    }

    /**
     * Function to return if the network is available or not
     * @return Boolean: True or False depending on whether network is available or not
     */
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Function to initialize all the lateinit variables
     */
    private fun initViews() {
        createGroupButton = findViewById(R.id.create_group_button)
        noGroupsAvailableImageView = findViewById(R.id.empty_state_image_view)
        recyclerViewForGroupList = findViewById(R.id.recyclerViewGroup)
        mProgressBar = findViewById(R.id.groups_progress_bar)
    }

    /**
     * Function to initialize groupViewModel
     */
    private fun initViewModel() {
        groupViewModel = ViewModelProviders.of(this)
            .get(GroupViewModel::class.java)
    }

    /**
     * Function to observe changes in group list live data
     */
    private fun observeData() {
        groupViewModel.getGroupListLiveData(ApplicationClass.connection.user.split("@")[0])
            .observe(this, Observer {
                if (!it.isNullOrEmpty()) {
                    groupList.clear()
                    recyclerViewForGroupList.visibility = View.VISIBLE
                    noGroupsAvailableImageView.visibility = View.GONE
                    groupList.addAll(it)
                    initRecyclerView()
                    groupListAdapter?.notifyDataSetChanged()
                } else {
                    recyclerViewForGroupList.visibility = View.GONE
                    noGroupsAvailableImageView.visibility = View.VISIBLE
                }
            })
    }

    /**
     * This function helps to initialize Recycler view
     */
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerViewForGroupList.layoutManager = layoutManager
        groupListAdapter =
            GroupListAdapter(
                groupList, this
            )
        recyclerViewForGroupList.adapter = groupListAdapter
    }

    /**
     * Function to initialize the click listeners of buttons present in the activity
     * On click of createGroup button, navigates to the GroupDetails activity
     */
    private fun initClickListener() {
        createGroupButton.setOnClickListener {
            startActivity(Intent(this, GroupDetailsActivity::class.java))
        }
    }

    /**
     * onStart overridden function
     * Calls Function to start the group service
     */
    override fun onStart() {
        super.onStart()
        startGroupService()
    }

    /**
     * Function to start group service
     * Registers group broadcast receiver
     */
    private fun startGroupService() {
        startService(Intent(this, GroupService::class.java))
    }

    /**
     * onStop overridden function
     * Calls Function to stop the group service
     */
    override fun onStop() {
        super.onStop()
        stopGroupService()
    }

    /**
     * Function to stop group service
     * UnRegisters group broadcast receiver
     */
    private fun stopGroupService() {
        stopService(Intent(this, GroupService::class.java))
    }

    /**
     * Function to handle onClick event of each group
     * Navigates to the chat activity of the selected group
     */
    override fun onCellClickListener(groupData: GroupDatabaseModel) {
        mProgressBar.visibility = View.VISIBLE
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("GROUP_ID", groupData.groupName)
        startActivity(intent)
        mProgressBar.visibility = View.GONE
    }

}
