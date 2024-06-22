package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.InternalReasonConstants;

import java.util.List;

/**
 * Gives the User feedback that the command had a problem.
 */
public class InvalidCommandFeedbackPacket extends AbstractPacket {
    /**
     * @param internalReason   for options see {@link InternalReasonConstants}
     * @param command          command which was executed by the client that caused the problem
     * @param displayMessage   message that shall be displayed on the client.
     * @param argument         argument in the command that caused the problem.
     * @param permissionNeeded permission required for that command / argument
     * @param userPermissions  permissions the user has.
     */
    public InvalidCommandFeedbackPacket(InternalReasonConstants internalReason, String command, String displayMessage, String argument, String permissionNeeded, List<String> userPermissions) {
        super(1, 1); //Min and Max supportet Version
        this.internalReason = internalReason;
        this.argument = argument;
        this.permissionNeeded = permissionNeeded;
        this.userPermissions = userPermissions;
        this.command = command;
        this.displayMessage = displayMessage;
    }

    public final InternalReasonConstants internalReason;
    public final String argument;
    public final String permissionNeeded;
    public final List<String> userPermissions;
    public final String command;
    public final String displayMessage;

}
