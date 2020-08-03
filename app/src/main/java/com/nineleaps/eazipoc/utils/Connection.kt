package com.nineleaps.eazipoc.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.services.ConnectionService
import org.jivesoftware.smack.*
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart
import java.io.IOException
import java.net.InetAddress


class Connection(context: Context) : ConnectionListener {
    private val TAG = "Connection"

    private var mApplicationContext: Context? = null
    private var mPhoneNumber: String? = null
    private var name: String? = null
    private var isLoggedIn: Boolean? = null
    private var mServiceName: DomainBareJid? = null
    private var mConnection: XMPPTCPConnection? = null

    enum class ConnectionState {
        CONNECTED, AUTHENTICATED, DISCONNECTED
    }

    init {
        Log.d(TAG, "Connection Constructor called.")
        mApplicationContext = context.applicationContext
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        mPhoneNumber = sharedPreferences.getString("xmpp_jid", null)
        name = sharedPreferences.getString("xmpp_name", null)
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
    }

    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {
        mServiceName = JidCreate.domainBareFrom("localhost")
        Log.d(TAG, "Connecting to server $mServiceName")
        val addr = InetAddress.getByName("192.168.0.109")
        val config = XMPPTCPConnectionConfiguration.builder()
            .setHost("nineleaps")
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setXmppDomain(mServiceName)
            .setSendPresence(true)
            .setHostAddress(addr)
            .setPort(5222)
            .build()
        ApplicationClass.connection = XMPPTCPConnection(config)
        mConnection = ApplicationClass.connection
        try {
            mConnection?.addConnectionListener(this)
            mConnection?.connect()
            if (mConnection?.isConnected!!) {
                if (!isLoggedIn!!) {
                    val accountManager = AccountManager.getInstance(mConnection)
                    accountManager.sensitiveOperationOverInsecureConnection(true)
                    if (mPhoneNumber != null && name != null) {
                        val hashMap = HashMap<String, String>()
                        hashMap["username"] = mPhoneNumber!!
                        hashMap["password"] = "pass"
                        hashMap["name"] = name!!
                        SASLAuthentication.unBlacklistSASLMechanism("PLAIN")
                        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5")
                        accountManager.createAccount(Localpart.from(mPhoneNumber), "pass", hashMap)
                    }
                }
                mConnection?.login(mPhoneNumber, "pass")
            }
        } catch (e: Exception) {
            Log.d("ApplicationKrishna", e.toString())
        }

    }

    private fun showContactListActivityWhenAuthenticated() {
        val i = Intent(ConnectionService.UI_AUTHENTICATED)
        i.setPackage(mApplicationContext?.packageName)
        mApplicationContext?.sendBroadcast(i)
        Log.d(TAG, "Sent the broadcast that we are authenticated")
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from server $mServiceName")
        try {
            if (mConnection != null) {
                mConnection?.disconnect()
            }

        } catch (e: SmackException.NotConnectedException) {
            ConnectionService.sConnectionState =
                ConnectionState.DISCONNECTED
            e.printStackTrace()
        }
        mConnection = null

    }

    override fun connected(connection: XMPPConnection?) {
        ConnectionService.sConnectionState =
            ConnectionState.CONNECTED
        Log.d(TAG, "Connected Successfully")
    }

    override fun connectionClosed() {
        ConnectionService.sConnectionState =
            ConnectionState.DISCONNECTED
        Log.d(TAG, "Connectionclosed()")
    }

    override fun connectionClosedOnError(e: Exception?) {
        ConnectionService.sConnectionState =
            ConnectionState.DISCONNECTED
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString())
    }

    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        ConnectionService.sConnectionState =
            ConnectionState.AUTHENTICATED
        Log.d(TAG, "Authenticated Successfully")
        showContactListActivityWhenAuthenticated()
    }

}