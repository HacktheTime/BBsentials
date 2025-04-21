package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.constants.AuthenticationConstants;

/**
 * From Client to Server. Tell the Server they want to connect.
 */
public class RequestConnectPacket extends AbstractPacket {

    /**
     * @param mcuuid           the mcuuid of the peron that wants to connect.
     * @param key              api key
     * @param clientApiVersion
     * @param authType
     */
    public RequestConnectPacket(String mcuuid, String key, String mcVersion, String modVersion, int clientApiVersion, AuthenticationConstants authType) {
        super(1, 1); //Min and Max supported Version
        this.mcuuid = mcuuid;
        this.key = key;
        this.mcVersion = mcVersion;
        this.modVersion = modVersion;
        this.authType = authType;
        this.clientApiVersion = clientApiVersion;
    }

    public final String mcuuid;
    public final String key;
    public final String mcVersion;
    public final String modVersion;
    public final AuthenticationConstants authType;
    public final int clientApiVersion;
}
