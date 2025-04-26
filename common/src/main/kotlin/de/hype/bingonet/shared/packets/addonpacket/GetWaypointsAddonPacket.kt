package de.hype.bingonet.shared.packets.addonpacket

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket
import de.hype.bingonet.shared.objects.ClientWaypointData

/**
 * Used to tell the addon what message came in.
 */
class GetWaypointsAddonPacket(val waypoints: MutableList<ClientWaypointData>) : AbstractAddonPacket(1, 1)
