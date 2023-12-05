package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

/**
 * Server to Client. When Client authenticated.
 */
public class WelcomeClientPacket extends AbstractPacket {
    /**
     * @param roles   the User has
     * @param motd    Current MOTD message
     * @param success Whether Auth was successful or not
     */
    public WelcomeClientPacket(String[] roles, String motd, boolean success) {
        super(1, 1); //Min and Max supportet Version
        this.roles = roles;
        this.motd = motd;
        this.success = success;
    }

    public final String[] roles;
    public final String motd;
    public final boolean success;

}