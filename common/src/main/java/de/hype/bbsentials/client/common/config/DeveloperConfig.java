package de.hype.bbsentials.client.common.config;

/**
 * This class and it uses are sensitive. Do not modify this class and its uses without permission!.
 * Sharing modified versions of this code ALWAYS requires Permission by the Project Owner (hackthetime)
 */

import de.hype.bbsentials.client.common.client.BBsentials;


public class DeveloperConfig extends BBsentialsConfig {
    public boolean devMode = false;
    public boolean detailedDevMode = false;
    public boolean doDevDashboardConfig = true;
    public boolean devSecurity = true;
    public boolean hypixelItemInfo = false;

    public DeveloperConfig() {
        super(1);
        doInit();
    }

    public boolean isDevModeEnabled() {
        return devMode && hasDevPerm();
    }

    public boolean isDetailedDevModeEnabled() {
        return detailedDevMode && hasDevPerm();
    }

    public boolean isDevSecurity() {
        return devSecurity && hasDevPerm();
    }

    @Override
    public void setDefault() {

    }

    public void onInit() {

    }

    private boolean hasDevPerm() {
        return BBsentials.generalConfig.hasBBRoles("dev");
    }
}
