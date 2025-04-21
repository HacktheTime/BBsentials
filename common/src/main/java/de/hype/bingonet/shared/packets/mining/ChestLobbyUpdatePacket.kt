package de.hype.bingonet.shared.packets.mining

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import de.hype.bingonet.shared.constants.StatusConstants
import de.hype.bingonet.shared.objects.ChestLobbyData

/**
 * Client and Server. Updates ChChests Status.
 */
class ChestLobbyUpdatePacket
/**
 * @param lobby   one of the following types: [StatusConstants.OPEN], [StatusConstants.FULL], [StatusConstants.LEAVINGSOON], [StatusConstants.LEFT], [StatusConstants.CLOSED]
 */(@JvmField val lobby: ChestLobbyData) : AbstractPacket(1, 1)
