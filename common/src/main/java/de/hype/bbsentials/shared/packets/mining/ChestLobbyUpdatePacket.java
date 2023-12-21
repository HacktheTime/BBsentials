package de.hype.bbsentials.shared.packets.mining;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChestLobbyData;

import java.util.Date;
import java.util.List;

/**
 * Client and Server. Updates ChChests Status.
 */
public class ChestLobbyUpdatePacket extends AbstractPacket {
    public final int lobbyId;
    public final ChestLobbyData lobby;
    public final List<String> playersStillIn;
    public final Date closingTime;

    /**
     * @param lobbyId        id of the lobby
     * @param lobby          one of the following types: {@link StatusConstants#OPEN}, {@link StatusConstants#FULL}, {@link StatusConstants#LEAVINGSOON}, {@link StatusConstants#LEFT}, {@link StatusConstants#CLOSED}
     * @param playersStillIn Players that were still in when leaving the lobby.
     * @param closingTime    assumed closingTime.
     */
    public ChestLobbyUpdatePacket(int lobbyId, ChestLobbyData lobby, List<String> playersStillIn, Date closingTime) {
        super(1, 1); //Min and Max supported Version
        this.lobbyId = lobbyId;
        this.lobby = lobby;
        this.playersStillIn = playersStillIn;
        this.closingTime = closingTime;
    }
}
