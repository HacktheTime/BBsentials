package de.hype.bbsentials.client.common.config;

/**
 * This class and it uses are sensitive. Do not modify this class and its uses without permission!.
 * Sharing modified versions of this code ALWAYS requires Permission by the Project Owner (hackthetime)
 */
import de.hype.bbsentials.client.common.config.BBsentialsConfig;
@AToLoadBBsentialsConfig
public class DeveloperConfig implements BBsentialsConfig {
    public boolean devMode = false;
    public boolean detailedDevMode = false;
    public boolean devSecurity = true;

    public boolean isDevModeEnabled() {
        return devMode;
    }

    public boolean isDetailedDevModeEnabled() {
        return detailedDevMode;
    }

    public boolean isDevSecurity() {
        return devSecurity;
    }

    @Override
    public void setDefault() {

    }
}
