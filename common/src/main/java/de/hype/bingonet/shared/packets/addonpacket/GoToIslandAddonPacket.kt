package de.hype.bingonet.shared.packets.addonpacket;

import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.shared.constants.Islands;

public class GoToIslandAddonPacket extends AbstractAddonPacket {
    public final Islands island;

    protected GoToIslandAddonPacket(Islands island) {
        super(1, 1);
        this.island = island;
    }
}
