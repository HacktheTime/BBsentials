package de.hype.bbsentials.client.common.config;


public class HUDConfig extends BBsentialsConfig {
    public boolean useChChestHudOverlay = true;

    public HUDConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
