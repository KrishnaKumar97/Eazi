package com.nineleaps.eazipoc

import android.app.Application
import org.jivesoftware.smack.tcp.XMPPTCPConnection

class ApplicationClass : Application() {
    companion object {
        lateinit var connection: XMPPTCPConnection
    }
}