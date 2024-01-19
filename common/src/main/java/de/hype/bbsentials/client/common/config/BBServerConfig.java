package de.hype.bbsentials.client.common.config;


public class BBServerConfig extends BBsentialsConfig {
    public boolean overrideBingoTime = false;
    public boolean connectToBeta = false;
    public boolean useMojangAuth = true;
    public String bbServerURL = "static.88-198-149-240.clients.your-server.de";
    public String apiKey = "";

    public BBServerConfig() {
        super(1);
        doInit();
    }


    @Override
    public void setDefault() {

    }
}
