package de.hype.bingonet.shared.packets.network;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

public class CompletedGoalPacket extends AbstractPacket {
    public String name;
    public String username;
    public CompletionType completionType;
    public String internalId;
    public String lore;
    public Integer progress;
    public boolean shouldBroadcast;

    /**
     * @param username        username. Filled by the server
     * @param name            Goal name. Filled by the server
     * @param internalId      Bingo goal id (Hypixel id)
     * @param completionType  {@link CompletionType#GOAL BingoGoal} or {@link CompletionType#CARD Card}
     * @param lore            Bingo BingoGoal Description /
     * @param progress        progress on the Card with how many Goals Completed. -1 For Unknown. In case of Card the Amount that the user Already completed
     * @param shouldBroadcast allows you to tell the server whether you want this info to be broadcast to the other clients
     */
    public CompletedGoalPacket(String username, String name, String internalId, String lore, CompletionType completionType, Integer progress, boolean shouldBroadcast) {
        super(1, 1);
        this.username = username;
        this.name = name;
        this.completionType = completionType;
        this.internalId = internalId;
        this.progress = progress;
        this.lore = lore;
        this.shouldBroadcast = shouldBroadcast;
    }

    public enum CompletionType {
        CARD,
        GOAL
    }
}

