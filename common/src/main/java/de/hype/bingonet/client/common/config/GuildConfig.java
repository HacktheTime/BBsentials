package de.hype.bingonet.client.common.config;


public class GuildConfig extends BingoNetConfig {
    public boolean guildAdmin = false;

    public GuildConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
