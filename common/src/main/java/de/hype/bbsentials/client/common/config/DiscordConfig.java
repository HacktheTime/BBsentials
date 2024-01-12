package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig

public class DiscordConfig  implements BBsentialsConfig{
    public boolean discordIntegration=false;
    public String botToken = null;
    public boolean useBridgeBot = false;
    public boolean allowCustomCommands=false;


    @Override
    public void setDefault() {

    }
}
