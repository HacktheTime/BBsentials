package de.hype.bbsentials.forge.common.packets.packets;

import de.hype.bbsentials.forge.common.packets.AbstractPacket;

public class WelcomeClientPacket extends AbstractPacket {

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