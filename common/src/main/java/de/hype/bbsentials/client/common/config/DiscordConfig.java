package de.hype.bbsentials.client.common.config;


public class DiscordConfig extends BBsentialsConfig {
    public boolean discordIntegration = false;
    public String botToken = "";
    public boolean useBridgeBot = false;
    public boolean allowCustomCommands = false;
    public boolean deleteHistoryOnServerSwap = true;
    public boolean alwaysSilent = false;
    public String botOwnerUserId = "-1";

    public DiscordConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
