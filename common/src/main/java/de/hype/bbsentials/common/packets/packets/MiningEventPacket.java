package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.constants.enviromentShared.Islands;
import de.hype.bbsentials.common.constants.enviromentShared.MiningEvents;

/**
 * used to announce a mining event network wide. Can be used by both Client and Server to announce to each other.
 */
public class MiningEventPacket extends AbstractPacket {
    public final MiningEvents event;
    public final String username;
    public final Islands island;

    /**
     * @param event    Event happening {@link MiningEvents (available constants)}
     * @param username username of the announcer
     * @param island   Island Event is happening on. Options: {@link Islands#DWARVEN_MINES} , {@link Islands#CRYSTAL_HOLLOWS}
     * @throws Exception when the Island is invalid. Can be when the island is CH but event can only be in Dwarfen Mines
     */
    public MiningEventPacket(MiningEvents event, String username, Islands island) throws Exception {
        super(1, 1); //Min and Max supported Version
        this.event = event;
        this.username = username;
        if (island != Islands.CRYSTAL_HOLLOWS && island != Islands.DWARVEN_MINES)
            throw new Exception("Invalid Island!");
        if (island.equals(Islands.CRYSTAL_HOLLOWS)) {
            if (event.equals(MiningEvents.MITHRIL_GOURMAND) || event.equals(MiningEvents.RAFFLE) || event.equals(MiningEvents.GOBLIN_RAID)) {
                throw new Exception("The specified event can not happen on this Server");
            }
        }
        this.island = island;
    }
}
