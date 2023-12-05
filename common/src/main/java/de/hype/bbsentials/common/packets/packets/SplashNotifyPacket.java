package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.constants.enviromentShared.Islands;

/**
 * Server and Client. USed to announce to each other.
 */
public class SplashNotifyPacket extends AbstractPacket {

    /**
     * @param splashId         the id of the Splash. Only Server to Client
     * @param hub              hub number
     * @param splasherUsername mc username of the splasher
     * @param location         location where is splashed.
     * @param hubType          {@link Islands#HUB} or {@link Islands#DUNGEON_HUB}
     * @param extraMessage     custom message
     * @param lessWaste        whether the less waste System shall be used.
     */
    public SplashNotifyPacket(int splashId, int hub, String splasherUsername, String location, Islands hubType, String extraMessage, boolean lessWaste) {
        super(1, 1); //Min and Max supportet Version
        this.hub = hub;
        this.splashId = splashId;
        this.lessWaste = lessWaste;
        this.splasherUsername = splasherUsername;
        this.location = location;
        this.hubType = hubType;
        this.extraMessage = extraMessage;
    }

    public final int hub;
    public final int splashId;
    public final boolean lessWaste;
    public final String splasherUsername;
    public final String location;
    public final Islands hubType;
    public final String extraMessage;
}
