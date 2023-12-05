package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

/**
 * Client and Server. Updates Splash Status.
 */
public class SplashUpdatePacket extends AbstractPacket {
    //TODO convert to enum
    public static final String STATUS_WAITING = "Waiting";
    public static final String STATUS_FULL = "Full";
    public static final String STATUS_SPLASHING = "Splashing";
    public static final String STATUS_CANCELED = "Canceled";
    public static final String STATUS_DONE = "Done";

    /**
     * @param splashId id of the splash
     * @param status   one of the following types: {@link #STATUS_WAITING}, {@link #STATUS_FULL}, {@link #STATUS_SPLASHING}, {@link #STATUS_CANCELED}, {@link #STATUS_DONE}
     */
    public SplashUpdatePacket(int splashId, String status) {
        super(1, 1); //Min and Max supported Version
        this.splashId = splashId;
        this.status = status;
    }

    public final int splashId;
    public final String status;
}
