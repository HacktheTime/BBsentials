package de.hype.bbsentials.packets.packets;

import de.hype.bbsentials.packets.AbstractPacket;

public class PunishUserPacket extends AbstractPacket {
    public static final String PUNISHMENT_TYPE_BAN = "BAN";
    public static final String PUNISHMENT_TYPE_MUTE = "MUTE";

    public PunishUserPacket(String punishmentType,int userId, String username, String duration, String reason) {
        super(1, 1);
        this.type = punishmentType;
        this.username = username;
        this.userId = userId;
        this.duration = duration;
        this.reason = reason;
    }

    public final String username;
    public final String type;
    public final int userId;
    public final String duration;
    public final String reason;

}
