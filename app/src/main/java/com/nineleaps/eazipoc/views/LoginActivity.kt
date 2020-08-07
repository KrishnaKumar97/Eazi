package com.nineleaps.eazipoc.views

import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nineleaps.eazipoc.utils.Utils
import com.nineleaps.eazipoc.services.ConnectionService
import com.nineleaps.eazipoc.R
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var submitButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var sharedPreferences: SharedPreferences? = null

    /**
     * OnCreate overridden function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // calling function to make the activity display in full screen
        Utils.displayFullScreen(this)
        setContentView(R.layout.activity_login)
        initializeViews()
        initClickListeners()
    }

    /**
     * function to initialize all the lateinit variables
     */
    private fun initializeViews() {
        firstName = findViewById(R.id.first_name_edit_text)
        lastName = findViewById(R.id.last_name_edit_text)
        phoneNumber = findViewById(R.id.phone_number_edit_text)
        submitButton = findViewById(R.id.submit_button)
        progressBar = findViewById(R.id.progress_bar)
    }

    /**
     * Function to initialize the click listeners of buttons present in the activity
     * On click of submit button, calls ConnectionService to register with the server
     */
    private fun initClickListeners(){
        submitButton.setOnClickListener {
            // Calls function to validate input fields
            val fieldsValid = validateFields()
            if (fieldsValid) {
                sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                editor?.putString("xmpp_jid", phoneNumber.text.toString())
                editor?.putString("xmpp_name",firstName.text.toString()+" "+lastName.text.toString())?.apply()
                submitButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                startService(Intent(this, ConnectionService::class.java))
            }
        }
    }

    /**
     * Function to validate firstName, lastName and phoneNumber
     * If any of the field is empty or is not valid, sets the focus to the field
     */
    private fun validateFields(): Boolean {
        val firstNameStr = firstName.text.toString()
        val lastNameStr = lastName.text.toString()
        val phoneNumberStr = phoneNumber.text.toString()
        var cancel = false
        var focusView: View? = null

        // Checks if firstName is not empty and is valid
        if (TextUtils.isEmpty(firstNameStr)) {
            firstName.error = "First Name cannot be empty"
            focusView = firstName
            cancel = true
        } else if (!validateName(firstNameStr)) {
            firstName.error = "Not a valid name"
            focusView = firstName
            cancel = true
        }

        // Checks if lastName is not empty and is valid
        if (TextUtils.isEmpty(lastNameStr)) {
            lastName.error = "Last Name cannot be empty"
            focusView = lastName
            cancel = true
        } else if (!validateName(lastNameStr)) {
            lastName.error = "Not a valid name"
            focusView = lastName
            cancel = true
        }

        // Checks if phoneNumber is not empty and is valid
        if (TextUtils.isEmpty(phoneNumberStr)) {
            phoneNumber.error = "Phone Number cannot be empty"
            focusView = phoneNumber
            cancel = true
        } else if (!validatePhoneNumber(phoneNumberStr)) {
            phoneNumber.error = "Not a valid Phone Number"
            focusView = phoneNumber
            cancel = true
        }
        return if (!cancel) {
            true
        } else {
            focusView?.requestFocus()
            false
        }

    }

    /**
     * Function to check if the name is valid
     */
    private fun validateName(name: String): Boolean {
        return ((name != "") && (name.matches(Regex("^[a-zA-Z]*$"))))
    }

    /**
     * Function to check if the phone nummber is valid
     */
    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        if (!Pattern.matches("[a-zA-Z]+", phoneNumber)) {
            return phoneNumber.length == 10
        }
        return false
    }

    /**
     * onResume overridden function
     * If the user is authenticated, navigates to the GroupsActivity
     */
    override fun onResume() {
        super.onResume()
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ConnectionService.UI_AUTHENTICATED -> {
                        sharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.putBoolean("isLoggedIn", true)?.apply()
                        startActivity(Intent(this@LoginActivity, GroupsActivity::class.java))
                        finish()
                    }
                }
            }

        }
        val filter = IntentFilter(ConnectionService.UI_AUTHENTICATED)
        this.registerReceiver(mBroadcastReceiver, filter)
    }

    /**
     * onPause overridden function
     */
    override fun onPause() {
        super.onPause()
        if (mBroadcastReceiver != null)
            this.unregisterReceiver(mBroadcastReceiver)
    }

}
