package de.hype.bingonet.shared.packets.function;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.objects.SplashData;

/**
 * Server and Client. USed to announce to each other.
 */
public class SplashNotifyPacket extends AbstractPacket {

    public final SplashData splash;

    /**
     * @param splash {@link SplashData} is used for storing all the Information
     */
    public SplashNotifyPacket(SplashData splash) {
        super(1, 1); //Min and Max supportet Version
        this.splash = splash;
    }
}
