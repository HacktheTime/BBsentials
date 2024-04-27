package de.hype.bbsentials.client.common.config;


public class SplashConfig extends BBsentialsConfig {
    public boolean autoSplashStatusUpdates = true;
    public boolean showSplashStatusUpdates = true;
    public boolean useSplasherOverlay = true;
    public boolean showMusicPantsUsers = true;
    public transient String smallestHubName = null;
    public boolean showSmallestHub = false;
    public boolean defaultUseLessWaste = true;
    public String defaultExtraMessage = "";
    public boolean xpBoostHighlight = false;

    public SplashConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
