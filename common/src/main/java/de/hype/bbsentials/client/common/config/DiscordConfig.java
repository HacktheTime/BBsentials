package de.hype.bbsentials.client.common.config;


public class DiscordConfig extends BBsentialsConfig {
    public boolean discordIntegration = false;
    public String botToken = "";
    public boolean useBridgeBot = false;
    public boolean allowCustomCommands = false;
    public boolean deleteHistoryOnStart = true;
    public boolean alwaysSilent = false;
    public boolean doStartupMessage = true;
    public boolean sdkMainToggle = false;
    public boolean useRichPresence = false;
    public String botOwnerUserId = "-1";
    /**
     * disableTemporary can be modified by the Bot via a command so you dont get annoyed by its messages but can enable it from remote if you want so.
     */
    private boolean disableTemporary = false;

    public DiscordConfig() {
        super(1);
        doInit();

    }

    @Override
    public void setDefault() {

    }

    public boolean isDisableTemporary() {
        return disableTemporary;
    }

    public void setDisableTemporary(boolean disableTemporary) {
        this.disableTemporary = disableTemporary;
    }
}
