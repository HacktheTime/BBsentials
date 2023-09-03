package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.constants.enviromentShared.Islands;
import de.hype.bbsentials.packets.AbstractPacket;

public class SplashNotifyPacket extends AbstractPacket {


    public SplashNotifyPacket(int hub, String splasherUsername, String location, Islands hubType, String extraMessage, boolean lessWaste) {
        super(1, 1); //Min and Max supported Version
        this.hub = hub;
        this.lessWaste = lessWaste;
        this.splasherUsername = splasherUsername;
        this.location = location;
        this.hubType = hubType;
        this.extraMessage = extraMessage;
    }

    public final int hub;
    public final boolean lessWaste;
    public final String splasherUsername;
    public final String location;
    public final Islands hubType;
    public final String extraMessage;
}
