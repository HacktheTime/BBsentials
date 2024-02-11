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

    /**
     * @param name           username. Filled by the server
     * @param internalId     Bingo goal id (Hypixel id)
     * @param completionType {@link CompletionType#GOAL BingoGoal} or {@link CompletionType#CARD Card}
     * @param lore           Bingo BingoGoal Description /
     * @param progress       progress on the Card with how many Goals Completed. -1 For Unknown. In case of Card the Amount that the user Already completed
     */
    public CompletedGoalPacket(String name, String internalId, CompletionType completionType, String lore, Integer progress) {
        super(1, 1);
        this.name = name;
        this.completionType = completionType;
        this.internalId = internalId;
        this.progress = progress;
        this.lore = lore;
    }
}

