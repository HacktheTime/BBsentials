package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.StatusConstants;

/**
 * Client and Server. Updates Splash Status.
 */
public class SplashUpdatePacket extends AbstractPacket {

    /**
     * @param splashId id of the splash
     * @param status   one of the following types: {@link StatusConstants#WAITING}, {@link StatusConstants#FULL}, {@link StatusConstants#SPLASHING}, {@link StatusConstants#CANCELED}, {@link StatusConstants#DONEBAD}
     */
    public SplashUpdatePacket(int splashId, StatusConstants status) {
        super(1, 1); //Min and Max supported Version
        this.splashId = splashId;
        this.status = status;
    }

    public final int splashId;
    public final StatusConstants status;
}
