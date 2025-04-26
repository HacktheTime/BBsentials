package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket

class StatusUpdateAddonPacket(@JvmField var status: Status) : AbstractAddonPacket(1, 1)

