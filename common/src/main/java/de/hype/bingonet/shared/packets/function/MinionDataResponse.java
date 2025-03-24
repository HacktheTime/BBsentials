package de.hype.bingonet.shared.packets.function;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.objects.minions.Minions;

import java.util.Map;

public class MinionDataResponse extends AbstractPacket {
    public final Map<Minions, Integer> minions;
    public final Integer maxSlots;

    public MinionDataResponse(Map<Minions, Integer> minions, Integer maxSlots) {
        super(1, 1);
        this.minions = minions;
        this.maxSlots = maxSlots;
    }

    public static class RequestMinionDataPacket extends AbstractPacket {
        public RequestMinionDataPacket() {
            super(1, 1);
        }
    }
}
