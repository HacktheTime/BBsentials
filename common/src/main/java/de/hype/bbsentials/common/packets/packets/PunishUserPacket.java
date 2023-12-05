package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

/**
 * Send von Client to Server to punish a User in the Network with a Ban or Mute.
 */
public class PunishUserPacket extends AbstractPacket {
    public static final String PUNISHMENT_TYPE_BAN = "BAN";
    public static final String PUNISHMENT_TYPE_MUTE = "MUTE";

    /**
     * @param punishmentType {@link #PUNISHMENT_TYPE_BAN} or {@link #PUNISHMENT_TYPE_MUTE}
     * @param userId         optional with username. userid to punish
     * @param username       optional with userid. mc username to punish
     * @param duration       Duration of the punishment
     * @param reason         Reason for the punishment
     */
    public PunishUserPacket(String punishmentType, int userId, String username, String duration, String reason) {
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
