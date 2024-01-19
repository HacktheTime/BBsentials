package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;

/**
 * Used to tell the addon what message came in.
 */
public class ChatPromptAddonPacket extends AbstractAddonPacket {
    public final String commandToExecute;
    public final int timeTillReset;

    public ChatPromptAddonPacket(String commandToExecute, int timeTillReset) {
        super(1, 1); //Min and Max supported Version
        this.commandToExecute = commandToExecute;
        this.timeTillReset = timeTillReset;
    }
}
