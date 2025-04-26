package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

/**
 * Plays the specified sound path on the client.
 */
class PlaySoundAddonPacket(val path: String, val namespace: String) : AbstractAddonPacket(1, 1)
