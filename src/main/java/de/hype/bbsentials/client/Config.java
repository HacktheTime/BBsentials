package de.hype.bbsentials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.chat.Sender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
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
        username = MinecraftClient.getInstance().player.getName().getString();
        leaveKuudraAutomatically = true;
        leaveDungeonAutomatically = true;
        acceptReparty = true;
        if (username.equals("Hype_the_Time")) {
            nickname = "Hype";
            getNotifForParty = "nick";
        }
        else {
            nickname = "";
            getNotifForParty = "none";
        }
    }

    // Method to send the config to a server using sockets
    public void sendConfig(Config config, String host, int port) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            socket = new Socket(host, port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(config);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to replace the current config with a new one
    public void replaceConfig(Config newConfig) {
        try {
            // Get the class of the current config
            Class<? extends Config> currentClass = this.getClass();

            // Get the fields of the current config class
            Field[] currentFields = currentClass.getDeclaredFields();

            // Iterate through the fields
            for (Field field : currentFields) {
                // Exclude the socket field from being updated
                if (field.getName().equals("serverSocket") || field.getName().equals("clientSocket")) {
                    continue;
                }

                // Make the field accessible to modify its value
                field.setAccessible(true);

                // Get the corresponding field from the new config class
                Field newField = newConfig.getClass().getDeclaredField(field.getName());

                // Make the new field accessible to read its value
                newField.setAccessible(true);

                // Get the current value of the field
                Object currentValue = field.get(this);

                // Get the new value of the field
                Object newValue = newField.get(newConfig);

                // Update the field only if it is defined or explicitly overridden
                if (newValue != null || currentValue == null) {
                    field.set(this, newValue);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
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
            }
        }
        else {
            settings = new Config(); // Use default values if the file doesn't exist
            settings.username = MinecraftClient.getInstance().player.getName().getString();
        }
        if (!settings.bbsentialsRoles.contains("dev")) {
            settings.detailedDevMode = false;
            settings.devMode = false;
        }
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
