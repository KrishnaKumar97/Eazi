package com.nineleaps.eazipoc.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.nineleaps.eazipoc.ApplicationClass
import com.nineleaps.eazipoc.services.ConnectionService
import org.jivesoftware.smack.*
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart
import org.jxmpp.stringprep.XmppStringprepException
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException


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

    /**
     * Function to establish a connection to the server
     */
    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {
        try {
            mServiceName = JidCreate.domainBareFrom("ip-172-31-14-161.us-east-2.compute.internal")
            Log.d(TAG, "Connecting to server $mServiceName")
            val addr = InetAddress.getByName("52.14.229.27")
            val config = XMPPTCPConnectionConfiguration.builder()
                .setHost("ip-172-31-14-161.us-east-2.compute.internal")
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
                            accountManager.createAccount(
                                Localpart.from(mPhoneNumber),
                                "pass",
                                hashMap
                            )
                        }
                    }
                    mConnection?.login(mPhoneNumber, "pass")
                }
            } catch (e: SmackException) {
                Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: XMPPException) {
                Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: InterruptedException) {
                Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: XmppStringprepException) {
            Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: UnknownHostException) {
            Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Function to send a broadcast telling that the authentication is successfull with the server
     */
    private fun postAuthentication() {
        val i = Intent(ConnectionService.UI_AUTHENTICATED)
        i.setPackage(mApplicationContext?.packageName)
        mApplicationContext?.sendBroadcast(i)
        Log.d(TAG, "Sent the broadcast that we are authenticated")
    }

    /**
     * Function to disconnect from the server
     */
    fun disconnect() {
        Log.d(TAG, "Disconnecting from server $mServiceName")
        try {
            if (mConnection != null) {
                mConnection?.disconnect()
            }
        } catch (e: SmackException.NotConnectedException) {
            ConnectionService.sConnectionState =
                ConnectionState.DISCONNECTED
            Toast.makeText(mApplicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
        mConnection = null

    }

    /**
     * Override function executed when connection is successful
     */
    override fun connected(connection: XMPPConnection?) {
        ConnectionService.sConnectionState =
            ConnectionState.CONNECTED
        Log.d(TAG, "Connected Successfully")
    }

    /**
     * Override function executed when connection is closed
     */
    override fun connectionClosed() {
        ConnectionService.sConnectionState =
            ConnectionState.DISCONNECTED
        Log.d(TAG, "Connectionclosed()")
    }

    /**
     * Override function executed when connection is closed due to an error
     */
    override fun connectionClosedOnError(e: Exception?) {
        ConnectionService.sConnectionState =
            ConnectionState.DISCONNECTED
        Log.d(TAG, "ConnectionClosedOnError, error " + e.toString())
    }

    /**
     * Override function executed when connection is authenticated
     */
    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        ConnectionService.sConnectionState =
            ConnectionState.AUTHENTICATED
        Log.d(TAG, "Authenticated Successfully")
        postAuthentication()
    }

}