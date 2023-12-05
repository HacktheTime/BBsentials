package de.hype.bbsentials.common.packets.packets;


import de.hype.bbsentials.common.packets.AbstractPacket;

/**
 * From Server to client telling him to authenticate.
 */
public class RequestAuthentication extends AbstractPacket {
    public final String serverIdSuffix;
    public final int serverVersion;

    /**
     * @param serverIdSuffix needed for Mojang Auth. "client" + "server" = serverid at mojang.
     * @param serverVersion  the version the server is on.
     */
    public RequestAuthentication(String serverIdSuffix, int serverVersion) {
        super(1, 1); //Min and Max supported Version
        this.serverIdSuffix = serverIdSuffix;
        this.serverVersion = serverVersion;
    }
}
