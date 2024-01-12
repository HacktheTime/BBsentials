package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig

public class HUDConfig implements BBsentialsConfig{
    public boolean useChChestHudOverlay = true;
    public boolean useSplashLeecherOverlayHud = true;
    public boolean showMusicPants = true;

    @Override
    public void setDefault() {

    }
}
