package de.hype.bbsentials.packets.packets;

import de.hype.bbsentials.packets.AbstractPacket;

public class InvalidCommandFeedbackPacket extends AbstractPacket {

    public InvalidCommandFeedbackPacket(String internalReason, String command, String displayMessage, String argument, String permissionNeeded, String[] userPermissions) {
        super(1, 1); //Min and Max supportet Version
        this.internalReason = internalReason;
        this.argument = argument;
        this.permissionNeeded = permissionNeeded;
        this.userPermissions = userPermissions;
        this.command = command;
        this.displayMessage = displayMessage;
    }

    public final String internalReason;
    public final String argument;
    public final String permissionNeeded;
    public final String[] userPermissions;
    public final String command;
    public final String displayMessage;

}
