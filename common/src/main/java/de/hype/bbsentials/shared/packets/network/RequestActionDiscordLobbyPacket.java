package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

public class RequestActionDiscordLobbyPacket extends AbstractPacket {

    public final ActionType action;
    public final Long lobbyId;
    public final String lobbySecret;
    public final boolean initIfNull;

    public RequestActionDiscordLobbyPacket(ActionType action, Long lobbyId, String lobbySecret, boolean initIfNull) {
        super(1, 1);
        this.action = action;
        this.lobbyId = lobbyId;
        this.lobbySecret = lobbySecret;
        this.initIfNull = initIfNull;
    }

    public enum ActionType {
        JOIN,
        JOINVC,
        DISCONNECT,
        DISCONNECTVC,
        DELETE,
        REQUESTINFO
    }
}
