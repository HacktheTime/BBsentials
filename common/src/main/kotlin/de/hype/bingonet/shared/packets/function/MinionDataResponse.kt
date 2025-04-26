package de.hype.bingonet.shared.packets.function

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import de.hype.bingonet.shared.objects.minions.Minions

class MinionDataResponse(val minions: MutableMap<Minions, Int>?, val maxSlots: Int) : AbstractPacket(1, 1) {
    class RequestMinionDataPacket : AbstractPacket(1, 1)
}
