package de.hype.bbsentials.client.common.config;

@AToLoadBBsentialsConfig

public class VisualConfig implements BBsentialsConfig {
    public boolean showBingoChat = true;
    public boolean doDesktopNotifications = false;
    public boolean doGammaOverride = true;

    public VisualConfig() {
    }

    @Override
    public void setDefault() {

    }
}
