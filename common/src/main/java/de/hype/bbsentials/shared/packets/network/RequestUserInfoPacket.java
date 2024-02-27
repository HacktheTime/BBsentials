package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.PunishmentData;

import java.util.List;
import java.util.stream.Collectors;

public class RequestUserInfoPacket extends AbstractPacket {
    public final boolean requestUpToDateData;
    public final Integer bbUserId;
    public final String mcUsername;
    public final Long dcUserId;
    public final Integer cardCount;
    public final Integer bingoPoints;
    public final String displayPrefix;

    public List<String> roles;
    public List<PunishmentData> punishments;

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

    public List<PunishmentData> getActivePunishments() {
        return punishments.stream().filter(PunishmentData::isActive).collect(Collectors.toList());
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
