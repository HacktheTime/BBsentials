package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Used to tell the client to generate a clientside tellraw message.
 * Disabled as of now due to the potential security issue clickable commands are.
 */
class DisplayTellrawMessageAddonPacket(val rawJson: String) : AbstractAddonPacket(1, 1)
