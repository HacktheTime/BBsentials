package de.hype.bingonet.client.common.config;


public class HUDConfig extends BingoNetConfig {
    public boolean useChChestHudOverlay = true;

    public HUDConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
