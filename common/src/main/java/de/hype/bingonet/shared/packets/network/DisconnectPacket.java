package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.constants.InternalReasonConstants;

/**
 * Used to tell a client that they were disconnected by the Host.
 */
public class DisconnectPacket extends AbstractPacket {

    /**
     * @param internalReason      Reason for the disconnect {@link InternalReasonConstants}
     * @param waitBeforeReconnect Time before client shall try to reconnect.
     * @param randomExtraDelay    Max random delay that is added to put less stress on the Server
     * @param displayReason       Reason to be displayed why Client was disconnected
     * @param displayMessage      Message is shown on the client.
     */

    public DisconnectPacket(InternalReasonConstants internalReason, int[] waitBeforeReconnect, int randomExtraDelay, String displayReason, String displayMessage) {
        super(1, 1); //Min and Max supportet Version
        this.internalReason = internalReason;
        this.waitBeforeReconnect = waitBeforeReconnect;
        this.displayReason = displayReason;
        this.displayMessage = displayMessage;
        this.randomExtraDelay = randomExtraDelay;
    }

    public final InternalReasonConstants internalReason;
    public final int[] waitBeforeReconnect;
    public final int randomExtraDelay;
    public final String displayReason;
    public final String displayMessage;

}
