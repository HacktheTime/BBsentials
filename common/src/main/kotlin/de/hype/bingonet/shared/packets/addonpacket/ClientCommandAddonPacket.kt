package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Used to tell the client to execute a command clientside
 */
class ClientCommandAddonPacket(val command: String) : AbstractAddonPacket(1, 1)
