package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.BBRole;

import java.util.List;
import java.util.Set;

/**
 * Server to Client. When Client authenticated.
 */
public class WelcomeClientPacket extends AbstractPacket {
    public final Set<BBRole> roles;

    /**
     * @param roles   the User has
     * @param motd    Current MOTD message
     * @param success Whether Auth was successful or not
     */
    public WelcomeClientPacket(Set<BBRole> roles, String motd, boolean success) {
        super(1, 1); //Min and Max supportet Version
        this.roles = roles;
        this.motd = motd;
        this.success = success;
    }
    public final String motd;
    public final boolean success;

}