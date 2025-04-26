package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Used to tell the client to generate a clientside message.
 */
class DisplayClientsideMessageAddonPacket(val message: String, val formatting: String) : AbstractAddonPacket(1, 1)
