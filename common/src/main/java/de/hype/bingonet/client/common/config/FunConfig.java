package de.hype.bingonet.client.common.config;


import java.time.Instant;

public class FunConfig extends BingoNetConfig {
    public boolean swapActionBarChat = false;
    public boolean swapOnlyNormal = true;
    public boolean swapOnlyBingoNet = false;
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
