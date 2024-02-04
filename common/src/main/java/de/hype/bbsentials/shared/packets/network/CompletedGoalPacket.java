package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

public class CompletedGoalPacket extends AbstractPacket {
    public String name;
    public CompletionType completionType;
    public String internalId;
    public String lore;
    public Integer progress;

    public enum CompletionType {
        CARD,
        GOAL
    }

    public CompletedGoalPacket(String name, String internalId, CompletionType completionType, String lore, Integer progress) {
        super(1, 1);
        this.name = name;
        this.completionType = completionType;
        this.internalId = internalId;
        this.progress = progress;
        this.lore = lore;
    }
}

