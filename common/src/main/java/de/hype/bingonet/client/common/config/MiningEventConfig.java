package de.hype.bingonet.client.common.config;

public class MiningEventConfig extends BingoNetConfig {
    public boolean allEvents = true;
    public boolean blockChEvents = false;

    public String betterTogether = "NONE";
    public String doublePowder = "NONE";
    public String goneWithTheWind = "NONE";
    public boolean goblinRaid = false;
    public boolean mithrilGourmand = false;
    public boolean raffle = false;

    public MiningEventConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
