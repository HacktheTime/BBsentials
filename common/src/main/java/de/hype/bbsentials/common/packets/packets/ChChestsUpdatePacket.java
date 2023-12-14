package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.constants.enviromentShared.StatusConstants;
import de.hype.bbsentials.common.packets.AbstractPacket;

import java.util.Date;
import java.util.List;

/**
 * Client and Server. Updates ChChests Status.
 */
public class ChChestsUpdatePacket extends AbstractPacket {
    public final int lobbyId;
    public final StatusConstants status;
    public final List<String> playersStillIn;
    public final Date closingTime;

    /**
     * @param lobbyId        id of the lobby
     * @param status         one of the following types: {@link StatusConstants#OPEN}, {@link StatusConstants#FULL}, {@link StatusConstants#LEAVINGSOON}, {@link StatusConstants#LEFT}, {@link StatusConstants#CLOSED}
     * @param playersStillIn Players that were still in when leaving the lobby.
     * @param closingTime    assumed closingTime.
     */
    public ChChestsUpdatePacket(int lobbyId, StatusConstants status, List<String> playersStillIn, Date closingTime) {
        super(1, 1); //Min and Max supported Version
        this.lobbyId = lobbyId;
        this.status = status;
        this.playersStillIn = playersStillIn;
        this.closingTime = closingTime;
    }
}
