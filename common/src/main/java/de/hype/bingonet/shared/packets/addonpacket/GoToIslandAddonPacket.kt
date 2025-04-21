package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket
import de.hype.bingonet.shared.constants.Islands

class GoToIslandAddonPacket protected constructor(val island: Islands) : AbstractAddonPacket(1, 1)
