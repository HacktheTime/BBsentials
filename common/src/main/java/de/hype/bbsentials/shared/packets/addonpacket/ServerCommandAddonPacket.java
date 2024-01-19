package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Use to tell the client to execute a command.
 */
public class ServerCommandAddonPacket extends AbstractAddonPacket {
    public final String command;
    public final double timing;

    public ServerCommandAddonPacket(String command, double cooldown) {
        super(1, 1); //Min and Max supported Version
        this.command = command;
        this.timing = cooldown;
    }
}
