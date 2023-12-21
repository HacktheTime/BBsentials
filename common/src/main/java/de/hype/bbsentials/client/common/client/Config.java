package de.hype.bbsentials.client.common.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.client.common.chat.Sender;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Config implements Serializable {
    //DO NOT Change any of the following unless you know what you are doing!
    public static int apiVersion = 1;
    public static List<String> partyMembers = new ArrayList<>();
    public transient final Sender sender = new Sender();
    public boolean devMode = false;
    public boolean detailedDevMode = false;
    // Gson object for serialization
    private final transient Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // File object for storing the config
    private final transient File CONFIG_FILE = new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsential_settings.json");
    //You can change again
    public boolean allowServerPartyInvite = true;
    public boolean devSecurity = true;
    public transient String overwriteActionBar = "";
    public transient String alreadyReported = "";
    public String[] bbsentialsRoles = {""};
    public transient ToDisplayConfig toDisplayConfig = ToDisplayConfig.loadFromFile();
    public transient boolean highlightitem = false;
    public transient String lastChatPromptAnswer = null;
    // Set via load / default you may change these
    public boolean useNumCodes = true;
    public boolean overrideBingoTime = false;
    public boolean connectToBeta = false;
    public boolean useMojangAuth = true;

    public String bbServerURL = "static.88-198-149-240.clients.your-server.de";
    public String apiKey = "";
    public boolean showBingoChat = true;
    public boolean doAllChatCustomMenu = true;
    public boolean doPartyChatCustomMenu = true;
    public boolean doGuildChatCustomMenu = true;

    public boolean allowBBinviteMe = true;
    public boolean doDesktopNotifications = false;
    public boolean showSplashStatusUpdates = true;
    public boolean useSplashLeecherOverlayHud = true;
    public boolean showMusicPants = true;
    public boolean doGammaOverride = true;
    public boolean acceptReparty;
    public boolean autoSplashStatusUpdates;
    public String nickname;
    public String notifForMessagesType;
    // trols
    public boolean swapActionBarChat = false;
    public boolean swapOnlyNormal = true;
    public boolean swapOnlyBBsentials = false;
    // set automatically
    private transient boolean isPartyLeader;
    private transient String username;

    // Constructor
    public Config() {
        setDefaults();
    }

    // Load the config from file
    public static Config load() {
        Config settings;
        File CONFIG_FILE = new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsential_settings.json");
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        if (CONFIG_FILE.exists()) {
            try {
                FileReader reader = new FileReader(CONFIG_FILE);
                settings = GSON.fromJson(reader, Config.class);
            } catch (IOException | RuntimeException e) {
                System.err.println("Error loading config. Resetting it.");
                e.printStackTrace();
                settings = new Config();
                settings.save();
            }
        }
        else {
            settings = new Config(); // Use default values if the file doesn't exist
            settings.username = EnvironmentCore.mcUtils.getUsername();
        }
        if (!settings.hasBBRoles("dev")) {
            //Changing disallowed. doing will result in a permanent ban!
            settings.detailedDevMode = false;
            settings.devMode = false;
        }
        settings.save();
        return settings;
    }

    public static boolean isBingoTime() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        Boolean isBefore = currentDate.isAfter(lastDayOfMonth.minusDays(4));
        Boolean isInRange = currentDate.isBefore(firstDayOfMonth.plusDays(15));
        return isBefore || isInRange;
    }

    // Set default attribute values
    private void setDefaults() {
        username = EnvironmentCore.mcUtils.getUsername();
        acceptReparty = true;
        if (username.equals("Hype_the_Time")) {
            nickname = "Hype";
            notifForMessagesType = "nick";
            doDesktopNotifications = true;
        } //Gimmic for Developer due too things which dont make it into releases (bugs)
        else {
            nickname = "";
            notifForMessagesType = "none";
        }
    }

    // Save the config to file
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        toDisplayConfig.saveToFile();
    }

    // Getter methods for various config attributes
    public String getUsername() {
        return username;
    }

    public boolean isPartyLeader() {
        return isPartyLeader;
    }

    public void setIsLeader(boolean value) {
        isPartyLeader = value;
    }

    public String getNickname() {
        return nickname;
    }

    public String getNotifForParty() {
        return notifForMessagesType;
    }

    public boolean isDevModeEnabled() {
        return devMode;
    }

    public boolean isDetailedDevModeEnabled() {
        return detailedDevMode;
    }

    public String[] getPlayersInParty() {
        return partyMembers.toArray(new String[0]);
    }

    public void addReported(String playerName) {
        alreadyReported = alreadyReported + " , " + playerName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBBServerURL() {
        return bbServerURL;
    }

    public boolean overrideBingoTime() {
        return overrideBingoTime;
    }

    public String getLastChatPromptAnswer() {
        return lastChatPromptAnswer;
    }

    public void setLastChatPromptAnswer(String lastChatPromptAnswer) {
        this.lastChatPromptAnswer = lastChatPromptAnswer;
    }

    public boolean allowBBinviteMe() {
        return allowBBinviteMe;
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

    public int getApiVersion() {
        return apiVersion;
    }

    public String getMCUUID() {
        return EnvironmentCore.mcUtils.getMCUUID().replace("-", "");
    }
}
