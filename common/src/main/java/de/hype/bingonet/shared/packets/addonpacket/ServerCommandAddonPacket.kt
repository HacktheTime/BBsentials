package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Use to tell the client to execute a command.
 */
class ServerCommandAddonPacket(val command: String, val timing: Double) : AbstractAddonPacket(1, 1)
