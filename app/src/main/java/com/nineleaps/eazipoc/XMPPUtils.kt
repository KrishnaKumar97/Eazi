package com.nineleaps.eazipoc

import org.jivesoftware.smack.SmackException.NoResponseException
import org.jivesoftware.smack.SmackException.NotConnectedException
import org.jivesoftware.smack.XMPPException.XMPPErrorException
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smackx.vcardtemp.packet.VCard
import org.jxmpp.jid.impl.JidCreate


object XMPPUtils {
    @Throws(
        NoResponseException::class,
        XMPPErrorException::class,
        NotConnectedException::class,
        InterruptedException::class
    )
    fun saveMUCVCard(groupId: String, data: ByteArray, connection: XMPPTCPConnection) {
        val vCard = VCard()
        vCard.to = JidCreate.entityBareFrom(groupId)
        vCard.type = IQ.Type.set
        vCard.avatar = data
        // Also make sure to generate a new stanza id (the given vcard could be a vcard result), in which case we don't
        // want to use the same stanza id again (although it wouldn't break if we did)
        vCard.setStanzaId()
        connection.createStanzaCollectorAndSend(vCard).nextResultOrThrow<VCard>()
    }
}