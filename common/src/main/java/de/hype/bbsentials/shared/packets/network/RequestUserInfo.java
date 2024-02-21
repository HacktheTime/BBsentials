package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

import java.util.List;

public class RequestUserInfo extends AbstractPacket {
    public final boolean requestUpToDateData;
    public final Integer bbUserId;
    public final String mcUsername;
    public final Long dcUserId;
    public final Integer cardCount;
    public final Integer bingoPoints;
    public final String displayPrefix;

    public List<String> roles;

    //TODO completed cards by type?
    //TODO    public List<PunishmentData> punishments;
    public RequestUserInfo(boolean requestUpToDateData, Integer bbUserId, String mcUsername, Long dcUserId, Integer cardCount, Integer bingoPoints, String displayPrefix) {
        super(1, 1);

        this.requestUpToDateData = requestUpToDateData;
        this.bbUserId = bbUserId;
        this.mcUsername = mcUsername;
        this.dcUserId = dcUserId;
        this.cardCount = cardCount;
        this.bingoPoints = bingoPoints;
        this.displayPrefix = displayPrefix;
    }

    public RequestUserInfo(boolean requestUpToDateData, Integer bbUserId, String mcUsername, Long dcUserId) {
        super(1, 1);

        this.requestUpToDateData = requestUpToDateData;
        this.bbUserId = bbUserId;
        this.mcUsername = mcUsername;
        this.dcUserId = dcUserId;
        this.cardCount = null;
        this.bingoPoints = null;
        this.displayPrefix = null;
    }

    public static RequestUserInfo fromDCUserID(long userId, boolean requestUpToDateData) {
        return new RequestUserInfo(requestUpToDateData, null, null, userId);
    }
}
