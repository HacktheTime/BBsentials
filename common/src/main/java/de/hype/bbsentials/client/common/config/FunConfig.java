package de.hype.bbsentials.client.common.config;


import java.time.Instant;

public class FunConfig extends BBsentialsConfig {
    public boolean swapActionBarChat = false;
    public boolean swapOnlyNormal = true;
    public boolean swapOnlyBBsentials = false;
    public transient String overwriteActionBar = "";
    public Boolean hub29Troll = false;
    public Boolean hub17To29Troll = false;
    public boolean lowPlayTimeHelpers = false;
    public transient Instant lowPlaytimeHelperJoinDate = null;


    public FunConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
