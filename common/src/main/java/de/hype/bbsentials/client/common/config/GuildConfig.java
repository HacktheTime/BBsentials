package de.hype.bbsentials.client.common.config;


public class GuildConfig extends BBsentialsConfig {
    public boolean guildAdmin = false;

    public GuildConfig() {
        super(1);
        doInit();
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
