package com.nineleaps.eazipoc

import android.app.Application
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.muc.MultiUserChat

class ApplicationClass : Application() {
    companion object {
        lateinit var connection: XMPPTCPConnection
        lateinit var groupMuc: MultiUserChat
    }
}