package com.nineleaps.eazipoc

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import org.jivesoftware.smackx.search.UserSearch
import org.jivesoftware.smackx.search.UserSearchManager
import androidx.core.content.ContextCompat.getSystemService
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import androidx.core.content.ContextCompat.getSystemService
import com.nineleaps.eazipoc.models.UserModel
import org.jivesoftware.smackx.xdata.Form
import org.jxmpp.jid.impl.JidCreate
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService


class UserService : Service() {

    private val TAG = "UserService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null

    companion object {
        const val USERS_FETCHED = "Fetched Users"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()");
    }

    private fun start() {
        if (mThread == null || !mThread!!.isAlive) {
            mThread = Thread(Runnable {
                Looper.prepare()
                mTHandler = Handler()
                fetchUsers()
                Looper.loop()
            })
            mThread!!.start()
        }
    }

    private fun fetchUsers() {
        try {
            val manager = UserSearchManager(ApplicationClass.connection)
            val searchFormString = "search." + ApplicationClass.connection.xmppServiceDomain
            var searchForm: Form? = null
            val userList = ArrayList<UserModel>()
            searchForm = manager.getSearchForm(JidCreate.domainBareFrom(searchFormString))

            val answerForm = searchForm!!.createAnswerForm()

            val userSearch = UserSearch()
            answerForm.setAnswer("Username", true)
            answerForm.setAnswer("search", "*")

            val results =
                userSearch.sendSearchForm(
                    ApplicationClass.connection,
                    answerForm,
                    JidCreate.domainBareFrom(searchFormString)
                )
            if (results != null) {
                val rows = results.rows
                for (row in rows) {
                    val userModel = UserModel()
                    userModel.jid = row.getValues("Username").toString()
                    userList.add(userModel)
                }
                val i = Intent(USERS_FETCHED)
                i.setPackage(applicationContext.packageName)
                i.putParcelableArrayListExtra("fetched_users", userList)
                applicationContext.sendBroadcast(i)
            } else {
                Log.d("***", "No result found")
            }

        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: XMPPException.XMPPErrorException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return START_STICKY
    }


}