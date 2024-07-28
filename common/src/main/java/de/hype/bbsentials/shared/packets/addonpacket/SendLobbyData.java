package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.client.common.client.BBDataStorage;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

import java.time.Instant;

public class SendLobbyData extends AbstractAddonPacket {
    public final BBDataStorage dataStorage;
    public final Instant lastPlaytimeUpdate;
    public final Instant serverJoinTime;

    public SendLobbyData(BBDataStorage dataStorage, Instant lastPlaytimeUpdate, Instant serverJoinTime) {
        super(1, 1);
        this.dataStorage = dataStorage;
        this.lastPlaytimeUpdate = lastPlaytimeUpdate;
        this.serverJoinTime = serverJoinTime;
    }
}
