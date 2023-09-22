package de.hype.bbsentials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.chat.Sender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Config implements Serializable {
    //DO NOT Change any of the following unless you know what you are doing!
    public int apiVersion = 1;
     boolean devMode = false;
     boolean detailedDevMode = false;
    //You can change again

    // set automatically
    private transient boolean isLeader;
    private transient String alreadyReported = "";
    public String[] bbsentialsRoles = {""};
    public static ArrayList<String> partyMembers = new ArrayList<>();
    public transient ToDisplayConfig toDisplayConfig = ToDisplayConfig.loadFromFile();
    public transient final Sender sender = new Sender();
    public transient boolean highlightitem = false;
    public transient String lastChatPromptAnswer = null;
    private transient String username;

    // Set via load / default you may change these
    public boolean overrideBingoTime = false;
    public boolean connectToBeta = false;

    public String bbServerURL = "localhost";
     String apiKey = "";
    public boolean showBingoChat = true;
    public boolean allowBBinviteMe = true;
    public boolean doDesktopNotifications = false;
    public boolean showSplashStatusUpdates = true;
    public boolean acceptReparty;
    public boolean autoSplashStatusUpdates;
    public String nickname;
    public String NotifForPartyMessagesType;

    // Set default attribute values
    private void setDefaults() {
        username = MinecraftClient.getInstance().player.getName().getString();
        acceptReparty = true;
        if (username.equals("Hype_the_Time")) {
            nickname = "Hype";
            NotifForPartyMessagesType = "nick";
            doDesktopNotifications=true;
        } //Gimmic for Developer due too things which dont make it into releases (bugs)
        else {
            nickname = "";
            NotifForPartyMessagesType = "none";
        }
    }

    // Gson object for serialization
    private final transient Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // File object for storing the config
    private final transient File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "BBsential_settings.json");

    // Constructor
    public Config() {
        setDefaults();
    }

    // Load the config from file
    public static Config load() {
        Config settings;
        File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "BBsential_settings.json");
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                settings = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
                settings = new Config(); // Use default values if loading fails
                settings.save();
            } catch (IllegalStateException e) {
                System.out.println("Error loading config. Resetting it.");
                settings = new Config();
                settings.save();
            }
        }
        else {
            settings = new Config(); // Use default values if the file doesn't exist
            settings.username = MinecraftClient.getInstance().player.getName().getString();
        }
        if (!settings.hasBBRoles("dev")) {
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
        toDisplayConfig.saveToFile();
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
        return NotifForPartyMessagesType;
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


    public static boolean isBingoTime() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        Boolean isBefore = currentDate.isAfter(lastDayOfMonth.minusDays(4));
        Boolean isInRange = currentDate.isBefore(firstDayOfMonth.plusDays(15));
        return isBefore || isInRange;
    }

    public boolean overrideBingoTime() {
        return overrideBingoTime;
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

    public boolean hasBBRoles(String roleName) {
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
}
