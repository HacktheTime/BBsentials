package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket
import de.hype.bingonet.shared.objects.Message

/**
 * Used to tell the addon what message came in.
 */
class ReceivedPublicChatMessageAddonPacket(@JvmField val message: Message) : AbstractAddonPacket(1, 1)
