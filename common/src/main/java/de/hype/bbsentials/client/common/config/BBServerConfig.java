package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig

public class BBServerConfig implements BBsentialsConfig {
    public boolean overrideBingoTime = false;
    public boolean connectToBeta = false;
    public boolean useMojangAuth = true;
    public String bbServerURL = "static.88-198-149-240.clients.your-server.de";
    public String apiKey = "";


    @Override
    public void setDefault() {

    }
}
