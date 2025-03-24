package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

public class PunishedPacket extends AbstractPacket {
    public final boolean silentCrash;
    public final int exitCodeOnCrash;
    public final boolean modSelfRemove;
    public final boolean shouldModCrash;
    public final int warningTimeBeforeCrash;
    public final boolean disconnectFromNetworkOnLoad;
    public String type;


    public PunishedPacket(boolean silentCrash, int exitCodeOnCrash, boolean modSelfRemove, boolean shouldModCrash, int warningTimeBeforeCrash, boolean disconnectFromNetworkOnLoad, String type) {
        super(1, 1);
        if (disconnectFromNetworkOnLoad && !shouldModCrash)
            throw new IllegalArgumentException("User must be disconnected from Network to Crash");
        this.silentCrash = silentCrash;
        this.exitCodeOnCrash = exitCodeOnCrash;
        this.modSelfRemove = modSelfRemove;
        this.shouldModCrash = shouldModCrash;
        this.warningTimeBeforeCrash = warningTimeBeforeCrash;
        this.disconnectFromNetworkOnLoad = disconnectFromNetworkOnLoad;
        this.type = type;
    }
}
