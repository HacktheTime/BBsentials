package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class RequestConnectPacket extends AbstractPacket {


    public RequestConnectPacket(String mcuuid, String key, int clientApiVersion, String authType ) {
        super(1, 1); //Min and Max supported Version
        this.mcuuid = mcuuid;
        this.key = key;
        this.authType = authType;
        this.clientApiVersion = clientApiVersion;
    }

    public final String mcuuid;
    public final String key;
    public final String authType;
    public final int clientApiVersion;
}
