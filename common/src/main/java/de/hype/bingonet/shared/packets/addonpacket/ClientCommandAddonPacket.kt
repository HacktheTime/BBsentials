package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the client to execute a command clientside
 */
public class ClientCommandAddonPacket extends AbstractAddonPacket {
    public final String command;

    public ClientCommandAddonPacket(String command) {
        super(1, 1); //Min and Max supported Version
        this.command = command;
    }

}
