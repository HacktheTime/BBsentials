package de.hype.bbsentials.client.common.config;


public class SplashConfig extends BBsentialsConfig {
    public boolean autoSplashStatusUpdates = true;
    public boolean showSplashStatusUpdates = true;

    public SplashConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
