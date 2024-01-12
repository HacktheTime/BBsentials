package de.hype.bbsentials.shared.packets.network;

public class CompletedGoalPacket {
    public String name;
    public CompletionType completionType;
    public String internalId;
    public String lore;
    public Integer progress;

   enum CompletionType{
        CARD,
        GOAL
    }
    public CompletedGoalPacket(String name , String internalId, CompletionType completionType, String lore, Integer progress){
       this.name=name;
       this.completionType=completionType;
       this.internalId = internalId;
       this.progress=progress;
       this.lore=lore;
    }
}

