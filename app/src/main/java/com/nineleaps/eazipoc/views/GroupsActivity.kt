package com.nineleaps.eazipoc.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.nineleaps.Utils
import com.nineleaps.eazipoc.R

class GroupsActivity : AppCompatActivity() {
    private lateinit var createGroupButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        Utils.displayFullScreen(this)
        initViews()
        initClickListener()
    }

    private fun initViews(){
        createGroupButton = findViewById(R.id.create_group_button)
    }

    private fun initClickListener(){
        createGroupButton.setOnClickListener {
            startActivity(Intent(this,GroupDetailsActivity::class.java))
        }
    }
}
