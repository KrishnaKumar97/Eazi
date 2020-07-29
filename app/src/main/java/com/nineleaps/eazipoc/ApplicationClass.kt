package com.nineleaps.eazipoc

import android.app.Application

class ApplicationClass : Application() {
//    companion object {
//        var connectionObject: MutableLiveData<XMPPTCPConnection>? = null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        MyLoginTask().execute("")
//    }
//}
//
//class MyLoginTask : AsyncTask<String, Void, XMPPTCPConnection>() {
//    override fun doInBackground(vararg params: String?): XMPPTCPConnection {
//        val serviceName = JidCreate.domainBareFrom("localhost")
//        val addr = InetAddress.getByName("192.168.0.109")
//
//        val config = XMPPTCPConnectionConfiguration.builder()
//            .setHost("nineleaps")
//            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//            .setXmppDomain(serviceName)
//            .setHostAddress(addr)
//            .setPort(5222)
//            .build()
//
//        val connection = XMPPTCPConnection(config)
//        try {
//            connection.connect()
//        } catch (e: Exception) {
//            Log.d("app", e.toString())
//        }
//        return connection
//    }
//
//    override fun onPostExecute(result: XMPPTCPConnection) {
//        connectionObject?.postValue(result)
//    }

}