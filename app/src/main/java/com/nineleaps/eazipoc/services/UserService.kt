package com.nineleaps.eazipoc.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.jivesoftware.smackx.search.UserSearch
import org.jivesoftware.smackx.search.UserSearchManager
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import com.nineleaps.eazipoc.models.UserModel
import org.jivesoftware.smackx.xdata.Form
import org.jxmpp.jid.impl.JidCreate
import com.nineleaps.eazipoc.ApplicationClass
import org.jivesoftware.smackx.muc.MultiUserChatException

/**
 * Service which is responsible to fetch the list of users present in the server
 */
class UserService : Service() {

    private val TAG = "UserService"
    private var mThread: Thread? = null
    private var mTHandler: Handler? = null

    /**
     * USERS_FETCHED will be used to broadcast the list of users and in the broadcast receiver to receive the list of users
     */
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

    /**
     * Function to invoke method which fetches the users from the server in the background
     */
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

    /**
     * Function fetches the users from the server and send a broadcast intent with the list of users
     */
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
        } catch (e: InterruptedException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        start()
        return START_STICKY
    }


}