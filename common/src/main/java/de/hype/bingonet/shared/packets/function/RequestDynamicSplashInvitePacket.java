package de.hype.bingonet.shared.packets.function;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

/**
 * Tells the Server you want to get a invite to the specified splash id.
 */
public class RequestDynamicSplashInvitePacket extends AbstractPacket {

    public final Integer splashId;

    /**
     * @param splashId The id of the splash you want to get invited to.
     */
    public RequestDynamicSplashInvitePacket(Integer splashId) {
        super(1, 1); //Min and Max supportet Version
        this.splashId = splashId;
    }
}
