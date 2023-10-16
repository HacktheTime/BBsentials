package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;

public class RequestAuthentication extends AbstractPacket {

    public final String serverIdSuffix;
    public final int serverVersion;

    public RequestAuthentication(String serverIdSuffix, int serverVersion) {
        super(1, 1); //Min and Max supported Version
        this.serverIdSuffix = serverIdSuffix;
        this.serverVersion = serverVersion;
    }
}
