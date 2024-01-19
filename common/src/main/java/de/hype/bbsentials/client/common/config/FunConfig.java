package de.hype.bbsentials.client.common.config;


public class FunConfig extends BBsentialsConfig {
    public boolean swapActionBarChat = false;
    public boolean swapOnlyNormal = true;
    public boolean swapOnlyBBsentials = false;
    public transient String overwriteActionBar = "";

    public FunConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
