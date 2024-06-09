package de.hype.bbsentials.client.common.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import static de.hype.bbsentials.client.common.client.BBsentials.generalConfig;


public class GeneralConfig extends BBsentialsConfig {

    public transient int apiVersion = 1;
    public String[] bbsentialsRoles = {""};
    public boolean useNumCodes = true;
    public boolean doGuildChatCustomMenu = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doDesktopNotifications = false;
    public String nickname = "";
    public String notifForMessagesType = "NONE";
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
     * @param roleName required role. Modifying this method for things which require devmode may be violating the license and will result in a permanent blocking from the network!
     * @return true when user has the role
     */
    public boolean hasBBRoles(String roleName) {
        if (roleName == null) return true;
        if (roleName.isEmpty()) return true;
        if (roleName.equals("debug") && EnvironmentCore.debug.isDevEnv() ) return true;
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
            bingoCard=new BingoCardManager();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError("Error Trying to load Bingo Data.");
        }
    }

}
