package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Used to tell the addon what message came in.
 */
class ChatPromptAddonPacket(val commandToExecute: String, val timeTillReset: Int) : AbstractAddonPacket(1, 1)
