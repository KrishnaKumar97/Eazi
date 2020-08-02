package com.nineleaps.eazipoc.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.R

class GroupDetailsActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0
    private lateinit var inviteFriendsButton: MaterialButton
    private lateinit var groupName: TextInputEditText
    var focusView: View? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        Utils.displayFullScreen(this)
        checkPermissionAndLoadContacts()
        initViews()
        initClickListener()
    }

    private fun initViews() {
        inviteFriendsButton = findViewById(R.id.invite_friends_button)
        groupName = findViewById(R.id.group_name_edit_text)
    }

    private fun initClickListener() {
        inviteFriendsButton.setOnClickListener {
            if (TextUtils.isEmpty(groupName.text.toString()) || groupName.text.toString() == " ") {
                groupName.error = "Group Name cannot be empty"
                groupName.requestFocus()
            } else if (groupName.text.toString().contains(" ")) {
                groupName.error = "Group Name cannot contain space"
                groupName.requestFocus()
            } else {
                val intent = Intent(this, ListOfContactsActivity::class.java)
                intent.putExtra("group_name", groupName.text.toString())
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissionAndLoadContacts() {
        checkSelfPermission(Manifest.permission.READ_CONTACTS)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("READ_CONTACTS", "Permission Granted")
                }
                return
            }
        }
    }
}
