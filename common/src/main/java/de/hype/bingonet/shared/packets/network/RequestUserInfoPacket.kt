package de.hype.bingonet.shared.packets.network

import de.hype.bingonet.environment.packetconfig.AbstractPacket

class RequestUserInfoPacket : AbstractPacket {
    val requestUpToDateData: Boolean
    val bbUserId: Int?
    val mcUsername: String?
    val dcUserId: Long?
    val cardCount: Int?
    val bingoPoints: Int?
    val displayPrefix: String?

    var roles: MutableList<String?>? = null

    constructor(
        requestUpToDateData: Boolean,
        bbUserId: Int?,
        mcUsername: String?,
        dcUserId: Long?,
        cardCount: Int?,
        bingoPoints: Int?,
        displayPrefix: String?
    ) : super(1, 1) {
        this.requestUpToDateData = requestUpToDateData
        this.bbUserId = bbUserId
        this.mcUsername = mcUsername
        this.dcUserId = dcUserId
        this.cardCount = cardCount
        this.bingoPoints = bingoPoints
        this.displayPrefix = displayPrefix
    }

    constructor(requestUpToDateData: Boolean, bbUserId: Int?, mcUsername: String?, dcUserId: Long?) : super(1, 1) {
        this.requestUpToDateData = requestUpToDateData
        this.bbUserId = bbUserId
        this.mcUsername = mcUsername
        this.dcUserId = dcUserId
        this.cardCount = null
        this.bingoPoints = null
        this.displayPrefix = null
    }

    fun hasRole(role: String?): Boolean {
        return roles!!.contains(role)
    }

    companion object {
        fun fromDCUserID(userId: Long, requestUpToDateData: Boolean): RequestUserInfoPacket {
            return RequestUserInfoPacket(requestUpToDateData, null, null, userId)
        }
    }
}
