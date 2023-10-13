package de.hype.bbsentials.forge.common.packets.packets;

import de.hype.bbsentials.forge.common.constants.enviromentShared.Islands;
import de.hype.bbsentials.forge.common.constants.enviromentShared.MiningEvents;
import de.hype.bbsentials.forge.common.packets.AbstractPacket;

public class MiningEventPacket extends AbstractPacket {


    public final MiningEvents event;
    public final String username;
    public final Islands island;

    public MiningEventPacket(MiningEvents event, String username, Islands island) throws Exception {
        super(1, 1); //Min and Max supported Version
        this.event = event;
        this.username = username;
        if (island.equals(Islands.CRYSTAL_HOLLOWS)) {
            if (event.equals(MiningEvents.MITHRIL_GOURMAND) || event.equals(MiningEvents.RAFFLE) || event.equals(MiningEvents.GOBLIN_RAID)) {
                throw new Exception("The specified event can not happen on this Server");
            }
        }
        this.island = island;
    }
}
