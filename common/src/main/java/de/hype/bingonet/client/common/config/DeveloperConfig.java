package de.hype.bingonet.client.common.config;

/**
 * This class and it uses are sensitive. Do not modify this class and its uses without permission!.
 * Sharing modified versions of this code ALWAYS requires Permission by the Project Owner (hackthetime)
 */

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.shared.objects.BBRole;

/**
 * DO NOT MODIFY THIS CLASS! If you modify this class you can easily do things which will ban you from the network permanently without prior notice nor warning!
 */
public class DeveloperConfig extends BingoNetConfig {

    public boolean devMode = false;
    public boolean detailedDevMode = false;
    public boolean devSecurity = true;
    public boolean hypixelItemInfo = false;
    public boolean quickLaunch = false;

    public DeveloperConfig() {
        super(1);
        doInit();
    }

    /**
     * DO NOT MODIFY THIS METHOD! If you modify this method you can easily do things which will ban you from the network permanently without prior notice nor warning!
     */
    public boolean isDevModeEnabled() {
        return devMode && hasDevPerm();
    }

    /**
     * DO NOT MODIFY THIS METHOD! If you modify this method you can easily do things which will ban you from the network permanently without prior notice nor warning!
     */
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

    /**
     * DO NOT MODIFY THIS METHOD! If you modify this method you can easily do things which will ban you from the network permanently without prior notice nor warning!
     */
    private boolean hasDevPerm() {
        return BingoNet.generalConfig.hasBBRoles(BBRole.DEVELOPER);
    }
}
