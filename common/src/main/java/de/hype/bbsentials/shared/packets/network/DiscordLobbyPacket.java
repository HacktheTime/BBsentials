package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

import java.util.List;
import java.util.Map;

public class DiscordLobbyPacket extends AbstractPacket {
    public final List<Long> members;
    public final Type type;
    public final Long lobbyId;
    public final String lobbySecret;
    public final Long ownerId;
    public final Integer maxSize;
    public final Boolean locked;
    public final Map<String, String> metaData;

    protected DiscordLobbyPacket(List<Long> members, Type type, Long lobbyId, String lobbySecret, Long ownerId, Integer maxSize, Boolean locked, Map<String, String> metaData) {
        super(1, 1);
        this.members = members;
        this.type = type;
        this.lobbyId = lobbyId;
        this.lobbySecret = lobbySecret;
        this.ownerId = ownerId;
        this.maxSize = maxSize;
        this.locked = locked;
        this.metaData = metaData;
    }

    public enum Type {
        PUBLIC,
        PRIVATE
    }
}
