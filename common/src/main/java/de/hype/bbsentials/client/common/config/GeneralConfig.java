package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.time.LocalDate;import de.hype.bbsentials.client.common.config.*;


@AToLoadBBsentialsConfig
public class GeneralConfig implements BBsentialsConfig {

    public int configVersion = 1;
    public transient int apiVersion = 1;
    public String[] bbsentialsRoles = {""};
    public boolean useNumCodes = true;
    public boolean doGuildChatCustomMenu = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doDesktopNotifications = false;
    public String nickname="";
    public String notifForMessagesType = "NONE";


    public static boolean isBingoTime() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        Boolean isBefore = currentDate.isAfter(lastDayOfMonth.minusDays(4));
        Boolean isInRange = currentDate.isBefore(firstDayOfMonth.plusDays(15));
        return isBefore || isInRange;
    }

    public boolean hasBBRoles(String roleName) {
        if (roleName == null) return true;
        if (roleName.isEmpty()) return true;
        for (String role : bbsentialsRoles) {
            if (role.equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    public String getMCUUID() {
        return EnvironmentCore.utils.getMCUUID().replace("-", "");
    }

    public String getUsername() {
        return EnvironmentCore.utils.getUsername();
    }

    public int getApiVersion() {
        return apiVersion;
    }

    @Override
    public void setDefault() {

    }

    @Override
    public void onInit(BBsentialsConfig config) {
        if (config instanceof GeneralConfig) {
            if (((GeneralConfig) config).configVersion != apiVersion) {
                //In case of update with config updater can go here.
            }
        }
    }
}
