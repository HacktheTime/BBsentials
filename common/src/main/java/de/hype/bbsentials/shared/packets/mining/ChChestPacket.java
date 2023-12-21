package de.hype.bbsentials.shared.packets.mining;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.ChChestItems;

/**
 * Used to announce a found CHChest. Can be from Client to Server to announce global or from Server to Client for the public announce.
 */
public class ChChestPacket extends AbstractPacket {
    /**
     * @param lobbyId the id of the lobby
     * @param announcerUsername person who found the chest.
     * @param items             items contained in the chest. {@link ChChestItems (available constants)}
     * @param locationCoords    coordinates of the chest.
     * @param bbcommand         command that shall be executed in game to get an invitation to the party.
     * @param extraMessage      a message that is optional to be provided by the finder for custom additional info
     */
    public ChChestPacket(int lobbyId, String announcerUsername, ChChestItem[] items, String locationCoords, String bbcommand, String extraMessage) {
        super(1, 1); //Min and Max supported Version
        this.lobbyId = lobbyId;
        this.announcerUsername = announcerUsername;
        this.locationCoords = locationCoords;
        this.bbcommand = bbcommand;
        this.extraMessage = extraMessage;
        this.items = items;
    }

    public final int lobbyId;
    public final String announcerUsername;
    public final String locationCoords;
    public final String bbcommand;
    public final String extraMessage;
    public final ChChestItem[] items;

}
