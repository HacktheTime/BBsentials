package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.IOException;
import java.util.*;


public class GeneralConfig extends BBsentialsConfig {

    public transient int apiVersion = 1;
    public List<String> bbsentialsRoles = new ArrayList<>();
    public boolean useNumCodes = true;
    public boolean doGuildChatCustomMenu = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doDesktopNotifications = false;
    public String nickname = "";
    public String notifForMessagesType = "NONE";
    public Set<String> profileIds = new HashSet<>();
    public transient Boolean isAlt;
    private BingoCardManager bingoCard;

    public GeneralConfig() {
        super(1);
        doInit();
    }

    public BingoCardManager getBingoCard() {
        return bingoCard;
    }

    /**
     * @param roleName required role. Modifying this method for things which require devmode may be violating the license and will result in a permanent blocking from the network!
     * @return true when user has the role
     */
    public boolean hasBBRoles(String roleName) {
        if (roleName == null) return true;
        if (roleName.isEmpty()) return true;
        if (roleName.equals("debug") && EnvironmentCore.debug.isDevEnv()) return true;
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

    public UUID getMCUUIDID() {
        return EnvironmentCore.utils.getMCUUIDID();
    }

    public String getUsername() {
        return EnvironmentCore.utils.getUsername();
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setDefault() {

    }

    public void onInit() {
        try {
            bingoCard = new BingoCardManager();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError("Error Trying to load Bingo Data.");
        }
    }

    public boolean isAlt() {
        if (isAlt == null) isAlt = getUsername().equals(getAltName());
        return isAlt;
    }

    public String getAltName() {
        return "NPCforCommands";
    }

    public String getMainName() {
        return "Hype_the_Time";
    }
}
