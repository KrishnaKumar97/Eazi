package com.nineleaps.eazipoc.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.nineleaps.Utils
import com.nineleaps.eazipoc.GroupService
import com.nineleaps.eazipoc.R

class GroupsActivity : AppCompatActivity() {
    private lateinit var createGroupButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        startService(Intent(this, GroupService::class.java))
        Utils.displayFullScreen(this)
        initViews()
        initClickListener()
    }

    private fun initViews() {
        createGroupButton = findViewById(R.id.create_group_button)
    }

    private fun initClickListener() {
        createGroupButton.setOnClickListener {
            startActivity(Intent(this, GroupDetailsActivity::class.java))
        }
    }

}
