package de.hype.bbsentials.shared.packets.addonpacket;

import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.shared.constants.TravelEnums;

public class SetGoToIsland extends AbstractAddonPacket {
    public TravelEnums warp;

    public SetGoToIsland(TravelEnums warp) {
        super(1, 1);
        this.warp=warp;
    }
}

