package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig
public class MiningEventConfig implements BBsentialsConfig {
    public boolean allEvents = true;
    public boolean blockChEvents = false;

    public String betterTogether = "NONE";
    public String doublePowder = "NONE";
    public String goneWithTheWind = "NONE";
    public boolean goblinRaid = false;
    public boolean mithrilGourmand = false;
    public boolean raffle = false;

    @Override
    public void setDefault() {

    }
}
