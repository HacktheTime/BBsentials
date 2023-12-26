package de.hype.bbsentials.shared.packets.mining;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChestLobbyData;

/**
 * Client and Server. Updates ChChests Status.
 */
public class ChestLobbyUpdatePacket extends AbstractPacket {
    public final ChestLobbyData lobby;

    /**
     * @param lobby   one of the following types: {@link StatusConstants#OPEN}, {@link StatusConstants#FULL}, {@link StatusConstants#LEAVINGSOON}, {@link StatusConstants#LEFT}, {@link StatusConstants#CLOSED}
     */
    public ChestLobbyUpdatePacket(ChestLobbyData lobby) {
        super(1, 1); //Min and Max supported Version
        this.lobby = lobby;
    }
}
