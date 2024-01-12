package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig

public class FunConfig implements BBsentialsConfig{
    public boolean swapActionBarChat = false;
    public boolean swapOnlyNormal = true;
    public boolean swapOnlyBBsentials = false;
    public transient String overwriteActionBar = "";

    @Override
    public void setDefault() {

    }
}
