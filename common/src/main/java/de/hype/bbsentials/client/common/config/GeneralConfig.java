package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.objects.BBRole;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class GeneralConfig extends BBsentialsConfig {

    public transient int apiVersion = 1;
    public Set<BBRole> bbsentialsRoles = new HashSet<>();
    public boolean useNumCodes = true;
    public boolean doGuildChatCustomMenu = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doDesktopNotifications = false;
    public String nickname = "";
    public String notifForMessagesType = "NONE";
    public boolean didFirstBoot = false;
    private BingoCardManager bingoCard;
    public Set<String> profileIds = new HashSet<>();

    public GeneralConfig() {
        super(1);
        doInit();
    }

    public BingoCardManager getBingoCard() {
        return bingoCard;
    }

    /**
     * WARNING: This method is used for Dev checks. Some developer functions allow for a unfair advantage. If you want to do something ask before on other users. I am open to add new Developers if there is actually work put in but ask.
     *
     * @param role required role.
     * @return true when user has the role
     */
    public boolean hasBBRoles(BBRole role) {
        if (role == null) return true;
        if (role.equals(BBRole.DEBUG) && EnvironmentCore.debug.isDevEnv()) return true;
        return bbsentialsRoles.contains(role);
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

}
