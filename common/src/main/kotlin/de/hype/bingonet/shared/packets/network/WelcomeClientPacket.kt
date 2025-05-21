package de.hype.bingonet.shared.packets.network

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import de.hype.bingonet.shared.objects.BBRole

/**
 * Server to Client. When Client authenticated.
 */
class WelcomeClientPacket
/**
 * @param roles   the User has
 * @param motd    Current MOTD message
 * @param success Whether Auth was successful or not
 */(val roles: Set<BBRole>, val motd: String?, val success: Boolean) : AbstractPacket(1, 1)