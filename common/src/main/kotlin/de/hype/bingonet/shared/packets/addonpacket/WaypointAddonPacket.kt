package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket
import de.hype.bingonet.shared.objects.WaypointData

/**
 * Used to tell the addon what message came in.
 */
class WaypointAddonPacket(val waypoint: WaypointData, val waypointId: Int, val operation: Operation) :
    AbstractAddonPacket(1, 1) {
    enum class Operation {
        ADD,
        REMOVE,
        EDIT
    }
}
