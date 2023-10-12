package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.constants.enviromentShared.InternalReasonConstants;
import de.hype.bbsentials.common.packets.AbstractPacket;

public class DisconnectPacket extends AbstractPacket {

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
