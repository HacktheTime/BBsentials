package de.hype.bbsentials.shared.packets.mining;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.ChestLobbyData;

/**
 * Used to announce a found CHChest. Can be from Client to Server to announce global or from Server to Client for the public announce.
 */
public class ChChestPacket extends AbstractPacket {
    /**
     * @param lobby {@link de.hype.bbsentials.shared.objects.ChestLobbyData} object containing the data
     */
    public ChChestPacket(ChestLobbyData lobby) {
        super(1, 1); //Min and Max supported Version
        this.lobby = lobby;
    }

    public final ChestLobbyData lobby;
}
