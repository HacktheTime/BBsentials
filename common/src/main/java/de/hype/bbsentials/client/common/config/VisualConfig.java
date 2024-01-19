package de.hype.bbsentials.client.common.config;


public class VisualConfig extends BBsentialsConfig {
    public boolean showBingoChat = true;
    public boolean doDesktopNotifications = false;
    public boolean doGammaOverride = true;

    public VisualConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
