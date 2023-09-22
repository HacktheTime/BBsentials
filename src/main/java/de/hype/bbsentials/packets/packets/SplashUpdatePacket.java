package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class SplashUpdatePacket extends AbstractPacket {
    public static final String STATUS_WAITING = "Waiting";
    public static final String STATUS_FULL = "Full";
    public static final String STATUS_SPLASHING = "Splashing";
    public static final String STATUS_CANCELED = "Canceled";
    public static final String STATUS_DONE = "Done";


    public SplashUpdatePacket(int splashId, String status) {
        super(1, 1); //Min and Max supported Version
        this.splashId = splashId;
        this.status = status;
    }

    public final int splashId;
    public final String status;
}
