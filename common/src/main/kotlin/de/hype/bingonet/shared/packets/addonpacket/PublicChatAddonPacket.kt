package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Sends the specified message to the server.
 */
class PublicChatAddonPacket(val message: String, val timing: Double) : AbstractAddonPacket(1, 1)
