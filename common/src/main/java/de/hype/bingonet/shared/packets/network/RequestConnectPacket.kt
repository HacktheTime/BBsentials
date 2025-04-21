package de.hype.bingonet.shared.packets.network

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import de.hype.bingonet.shared.constants.AuthenticationConstants

/**
 * From Client to Server. Tell the Server they want to connect.
 */
class RequestConnectPacket
/**
 * @param mcuuid           the mcuuid of the peron that wants to connect.
 * @param key              api key
 * @param clientApiVersion
 * @param authType
 */(
    @JvmField val mcuuid: String?,
    @JvmField val key: String?,
    val mcVersion: String?,
    val modVersion: String?,
    val clientApiVersion: Int,
    @JvmField val authType: AuthenticationConstants?
) : AbstractPacket(1, 1)
