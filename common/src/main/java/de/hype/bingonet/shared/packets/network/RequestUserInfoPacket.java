package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

import java.util.List;

public class RequestUserInfoPacket extends AbstractPacket {
    public final boolean requestUpToDateData;
    public final Integer bbUserId;
    public final String mcUsername;
    public final Long dcUserId;
    public final Integer cardCount;
    public final Integer bingoPoints;
    public final String displayPrefix;

    public List<String> roles;

    public RequestUserInfoPacket(boolean requestUpToDateData, Integer bbUserId, String mcUsername, Long dcUserId, Integer cardCount, Integer bingoPoints, String displayPrefix) {
        super(1, 1);
        this.requestUpToDateData = requestUpToDateData;
        this.bbUserId = bbUserId;
        this.mcUsername = mcUsername;
        this.dcUserId = dcUserId;
        this.cardCount = cardCount;
        this.bingoPoints = bingoPoints;
        this.displayPrefix = displayPrefix;
    }

    public RequestUserInfoPacket(boolean requestUpToDateData, Integer bbUserId, String mcUsername, Long dcUserId) {
        super(1, 1);

        this.requestUpToDateData = requestUpToDateData;
        this.bbUserId = bbUserId;
        this.mcUsername = mcUsername;
        this.dcUserId = dcUserId;
        this.cardCount = null;
        this.bingoPoints = null;
        this.displayPrefix = null;
    }

    public static RequestUserInfoPacket fromDCUserID(long userId, boolean requestUpToDateData) {
        return new RequestUserInfoPacket(requestUpToDateData, null, null, userId);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
