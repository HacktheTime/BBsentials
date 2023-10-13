//Keep in mind that a lot of the stuff in here may never be used and is here because its copied from the 1.20 version which is the main part of the code.

package de.hype.bbsentials.forge.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.forge.chat.Chat;
import de.hype.bbsentials.forge.chat.Sender;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Config implements Serializable {
    // Helper class for sending chat messages
    public transient final Sender sender = new Sender();

    public transient boolean highlightitem = false;
    public transient String lastChatPromptAnswer = null;

    // Automatically set, no need for config
    private transient String username;
    private boolean overrideBingoTime = false;

    // Set in-game
    private transient boolean isLeader;
    private transient String alreadyReported = "";
    private String bbServerURL = "static.204.177.34.188.clients.your-server.de";
    public String bbsentialsRoles = "";
    public static ArrayList<String> partyMembers = new ArrayList<>();

    // Set via load / default
    private String bbsentialsCommandPrefix = ".";
    private String apiKey = "";
    private boolean leaveDungeonAutomatically;
    private boolean allowBBinviteMe = true;
    private boolean leaveKuudraAutomatically;
    private boolean devMode = false;
    private boolean detailedDevMode = false;
    private boolean acceptReparty;
    private String nickname;
    private String getNotifForParty;

    // Set default attribute values
    private void setDefaults() {
        username = Minecraft.getMinecraft().thePlayer.getName();
        leaveKuudraAutomatically = true;
        leaveDungeonAutomatically = true;
        acceptReparty = true;
        if (username.equals("Hype_the_Time")) {
            nickname = "hype";
            getNotifForParty = "nick";
        }
        else {
            nickname = "";
            getNotifForParty = "none";
        }
    }
    // Gson object for serialization
    private final transient Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // File object for storing the config
    private final transient File CONFIG_FILE = new File("./config/BBsential_settings.json");

    // Constructor
    public Config() {
        setDefaults();
    }

    // Load the config from file
    public static Config load() {
        Config settings;
        File CONFIG_FILE = new File("./config/BBsential_settings.json");
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                settings = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
                settings = new Config(); // Use default values if loading fails
            }
        }
        else {
            settings = new Config(); // Use default values if the file doesn't exist
        }
        if (!settings.bbsentialsRoles.contains("dev")) {
            settings.detailedDevMode = false;
            settings.devMode = false;
        }
        settings.save();
        return settings;
    }

    // Save the config to file
    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter methods for various config attributes
    public String getUsername() {
        return username;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setIsLeader(boolean value) {
        isLeader = value;
    }

    public String getNickname() {
        return nickname;
    }

    public String getNotifForParty() {
        return getNotifForParty;
    }

    public boolean isLeaveDungeonAutomatically() {
        return leaveDungeonAutomatically;
    }

    public boolean isLeaveKuudraAutomatically() {
        return leaveKuudraAutomatically;
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

    public boolean messageFromAlreadyReported(String message) {
        return alreadyReported.contains(Chat.getPlayerNameFromMessage(message));
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

    public String getCommandPrefix(String type) {
        if (type.equals("BBsentials")) {
            System.out.println("Registered command with: " + bbsentialsCommandPrefix);
            return bbsentialsCommandPrefix;
        }
        else {
            return "/";
        }
    }

    public static boolean isBingoTime() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        Boolean isBefore = currentDate.isAfter(lastDayOfMonth.minusDays(4));
        Boolean isInRange = currentDate.isBefore(firstDayOfMonth.plusDays(15));
        return isBefore || isInRange;
    }

    public boolean overrideBingoTime()  {
        return overrideBingoTime;
    }

    public boolean isHighlightitem() {
        return highlightitem;
    }

    public String getLastChatPromptAnswer() {
        return lastChatPromptAnswer;
    }

    public boolean allowBBinviteMe() {
        return allowBBinviteMe;
    }

    public void setLastChatPromptAnswer(String lastChatPromptAnswer) {
        this.lastChatPromptAnswer = lastChatPromptAnswer;
    }
}
