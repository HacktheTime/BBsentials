package de.hype.bingonet.shared.packets.network

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import de.hype.bingonet.shared.constants.Islands


/**
 * Used to find collect Data across Servers. Can be used to find Users or Lobbies by ID
 */
class WantedSearchPacket : AbstractPacket {
    var targetFound: Boolean = true
    var username: String?
    var island: Islands? = null
    var mega: Boolean? = null
    var minimumPlayerCount: Int? = null
    var maximumPlayerCount: Int? = null
    var serverId: String?

    constructor(mcUsername: String?, serverId: String?) : super(1, 1) {
        this.username = mcUsername
        this.serverId = serverId
    }

    constructor(
        mcUsername: String?,
        serverId: String?,
        island: Islands?,
        mega: Boolean?,
        minimumPlayerCount: Int?,
        maximumPlayerCount: Int?
    ) : super(1, 1) {
        this.username = mcUsername
        this.serverId = serverId
        this.mega = mega
        this.island = island
        this.minimumPlayerCount = minimumPlayerCount
        this.maximumPlayerCount = maximumPlayerCount
    }

    class WantedSearchPacketReply(
        @JvmField var finder: String,
        usernames: MutableList<String>,
        megaServer: Boolean,
        serverId: String
    ) : AbstractPacket(1, 1) {
        @JvmField
        var usernames: MutableList<String>
        var megaServer: Boolean
        var currentPlayerCount: Int

        @JvmField
        var serverId: String

        init {
            this.usernames = usernames
            this.megaServer = megaServer
            this.currentPlayerCount = usernames.size
            this.serverId = serverId
        }
    }

    companion object {
        fun findMcUser(username: String?): WantedSearchPacket {
            val packet = WantedSearchPacket(username, null)
            packet.targetFound = false
            return packet
        }

        @JvmStatic
        fun findServer(serverId: String?): WantedSearchPacket {
            val packet = WantedSearchPacket(null, serverId)
            packet.targetFound = false
            return packet
        }

        fun findPrivateMega(island: Islands?): WantedSearchPacket {
            val packet = WantedSearchPacket(null, null, island, true, null, 15)
            packet.targetFound = false
            return packet
        }

        fun findPrivateMegaHubServer(): WantedSearchPacket {
            return findPrivateMega(Islands.HUB)
        }
    }
}
