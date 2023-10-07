package de.hype.bbsentials.chat;

import de.hype.bbsentials.client.BBsentials;
import de.hype.bbsentials.client.Config;
import de.hype.bbsentials.packets.packets.SplashUpdatePacket;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hype.bbsentials.client.BBsentials.*;

public class Chat {
    public Chat() {
        init();
    }

    public static String[] getVariableInfo(String packageName, String className) {
        List<String> variableInfoList = new ArrayList<>();

        // Combine the class name with the package name
        String fullClassName = packageName + "." + className;

        // Load the class
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Extract fields of the class
        Field[] fields = clazz.getDeclaredFields();

        // Collect information for each field
        for (Field field : fields) {
            // Exclude transient fields
            if (java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            String variableName = field.getName();
            String variablePackageName = clazz.getPackage().getName();
            String variableClassName = clazz.getSimpleName();

            variableInfoList.add(variableName);
        }

        return variableInfoList.toArray(new String[variableInfoList.size()]);
    }

    public static void setVariableValue(Object obj, String variableName, String value) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (value == null) {
            // Handle null value case
            sendPrivateMessageToSelfError("Invalid value: null");
            return;
        }

        Class<?> objClass = obj.getClass();
        Field field = objClass.getDeclaredField(variableName);
        field.setAccessible(true);

        // Get the type of the field
        Class<?> fieldType = field.getType();

        // Convert the value to the appropriate type
        Object convertedValue = parseValue(value, fieldType);

        if (Modifier.isStatic(field.getModifiers())) {
            // If the field is static
            field.set(null, convertedValue);
        }
        else {
            field.set(obj, convertedValue);
        }

        // Check and output the value of the variable
        sendPrivateMessageToSelfSuccess("The variable " + field.getName() + " is now: " + field.get(obj));
    }

    private static Object parseValue(String value, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        }
        else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        }
        else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        }
        else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        else {
            // For other types, return the original string value
            return value;
        }
    }

    public static void getVariableValue(Object object, String variableName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> objClass = object.getClass();
        Field field = objClass.getDeclaredField(variableName);
        field.setAccessible(true);
        sendPrivateMessageToSelfSuccess("The variable " + field.getName() + " is: " + field.get(object));
    }

    private void init() {
        // Register a callback for a custom message type
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            onEvent(message);
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> (onEvent(message)));
        ClientSendMessageEvents.CHAT.register(message -> {
            if (message.startsWith("/")) {
                System.out.println("Sent command: " + message);
            }
        });
    }

    private Text onEvent(Text text) {
        if (!isSpam(text.toString())) {
            if (getConfig().isDetailedDevModeEnabled()) {
                System.out.println("got a message: " + Text.Serializer.toJson(text));
            }
            Message message = new Message(text);
            executionService.execute(() -> processThreaded(message));
            return processNotThreaded(message);
        }
        return text; // Return the original message if it is spam
    }

    //Handle in the messages which need to be modified here
    public Text processNotThreaded(Message message) {
//        if (message.isFromParty()) {
//           message.replaceInJson("\"action\":\"run_command\",\"value\":\"/viewprofile", "\"action\":\"run_command\",\"value\":\"/bviewprofile " + messageUnformatted.split(">", 1)[1].trim());
//        }
        if (message.isFromReportedUser()) {
            sendPrivateMessageToSelfBase(Formatting.RED + "B: " + message);
            return null;
        }
        return message.text;
    }

    public void processThreaded(Message message) {
        if (message.getString() != null) {
            String messageUnformatted = message.getUnformattedString();
            String username = message.getPlayerName();
            if (message.isFromReportedUser()) {

            }
            else if (!MinecraftClient.getInstance().isWindowFocused()) {
                if (config.doDesktopNotifications) {
                    if ((messageUnformatted.endsWith("is visiting Your Garden !") || messageUnformatted.endsWith("is visiting Your Island !")) && !MinecraftClient.getInstance().isWindowFocused() && config.doDesktopNotifications) {
                        sendNotification("BBsentials Visit-Watcher", messageUnformatted);
                    }
                    else if (message.isMsg()) {
                        sendNotification("BBsentials Message Notifier", username + " sent you the following message: " + message.getMessageContent());
                    }
                    else {
                        if (message.getMessageContent().toLowerCase().contains(getConfig().getUsername().toLowerCase()) || message.getMessageContent().toLowerCase().contains(config.getNickname().toLowerCase() + " ")) {
                            sendNotification("BBsentials Notifier", "You got mentioned in chat! " + message.getMessageContent());
                        }
                    }
                }
            }
            else if (message.isServerMessage()) {
                if (messageUnformatted.contains("disbanded the party")) {
                    lastPartyDisbandedUsername = username;
                    partyDisbandedMap.put(username, Instant.now());
                }
                else if (message.contains("invited you to join their party")) {
                    if (lastPartyDisbandedUsername != null && partyDisbandedMap != null) {
                        Instant lastDisbandedInstant = partyDisbandedMap.get(lastPartyDisbandedUsername);
                        if (config.acceptReparty) {
                            if (lastDisbandedInstant != null && lastDisbandedInstant.isAfter(Instant.now().minusSeconds(20)) && (username.equals(lastPartyDisbandedUsername))) {
                                sendCommand("/p accept " + username);
                            }
                        }
                    }
                    if (!MinecraftClient.getInstance().isWindowFocused()) {
                        sendNotification("BBsentials Party Notifier", "You got invited too a party by: " + username);
                    }

                }
                else if (message.startsWith("Party Members (")) {
                    Config.partyMembers = new ArrayList<>();
                }
                else if (message.startsWith("Party Moderators:")) {
                    String temp = messageUnformatted.replace("Party Moderators:", "").replace(" ●", "").replaceAll("\\s*\\[[^\\]]+\\]", "").trim();
                    if (temp.contains(",")) {
                        for (int i = 0; i < temp.split(",").length; i++) {
                            Config.partyMembers.add(temp.split(",")[i - 1]);
                        }
                    }
                    else {
                        Config.partyMembers.add(temp);
                    }
                }
                else if (message.startsWith("Party Members:")) {
                    String temp = messageUnformatted.replace("Party Members:", "").replace(" ●", "").replaceAll("\\s*\\[[^\\]]+\\]", "").trim();
                    if (temp.contains(",")) {
                        for (int i = 0; i < temp.split(",").length; i++) {
                            System.out.println("Added to plist: " + (temp.split(",")[i - 1]));
                            Config.partyMembers.add(temp.split(",")[i - 1]);
                        }
                    }
                    else {
                        Config.partyMembers.add(temp);
                    }
                }
                else if ((message.contains("Party Leader:") && !message.contains(getConfig().getUsername())) || message.equals("You are not currently in a party.") || (message.contains("warped the party into a Skyblock Dungeon") && !message.startsWith(getConfig().getUsername()) || (!message.startsWith("The party was transferred to " + getConfig().getUsername()) && message.startsWith("The party was transferred to"))) || message.equals(getConfig().getUsername() + " is now a Party Moderator") || (message.equals("The party was disbanded because all invites expired and the party was empty.")) || (message.contains("You have joined ") && message.contains("'s party!")) || (message.contains("Party Leader, ") && message.contains(" , summoned you to their server.")) || (message.contains("warped to your dungeon"))) {
                    BBsentials.getConfig().setIsLeader(false);
                    if (getConfig().isDetailedDevModeEnabled()) {
                        sendPrivateMessageToSelfDebug("Leader: " + getConfig().isLeader());
                    }
                }
                else if ((messageUnformatted.equals("Party Leader: " + getConfig().getUsername() + " ●")) || (message.contains(getConfig().getUsername() + " warped the party to a SkyBlock dungeon!")) || message.startsWith("The party was transferred to " + getConfig().getUsername()) || message.equals("Raul_J has promoted " + getConfig().getUsername() + " to Party Leader") || (message.contains("warped to your dungeon"))) {
                    BBsentials.getConfig().setIsLeader(true);
                    if (getConfig().isDetailedDevModeEnabled()) {
                        sendPrivateMessageToSelfDebug("Leader: " + getConfig().isLeader());
                    }
                }
                else if (message.getUnformattedString().equals("Please type /report confirm to log your report for staff review.")) {
                    sendCommand("/report confirm");
                }
                else if (messageUnformatted.startsWith("BUFF! You splashed yourself with")) {
                    if (splashStatusUpdateListener != null) {
                        splashStatusUpdateListener.setStatus(SplashUpdatePacket.STATUS_SPLASHING);
                    }
                }
            }

            else if (message.isFromGuild()) {

            }
            else if (message.isFromParty()) {
                if (message.getMessageContent().toLowerCase().contains(getConfig().getUsername().toLowerCase()) || (message.getMessageContent().toLowerCase().contains(getConfig().getNickname().toLowerCase() + " ") && getConfig().getNotifForParty().toLowerCase().equals("nick")) || getConfig().getNotifForParty().toLowerCase().equals("all")) {
                    sendNotification("BBsentials Party Chat Notification", username + " : " + message.getMessageContent());
                }
            }
            else if (message.isMsg()) {
                if (messageUnformatted.endsWith("bb:party me")) {
                    if (BBsentials.getConfig().allowBBinviteMe()) {
                        sendCommand("/p " + username);
                    }
                }
            }
            else {
                if (message.contains("[OPEN MENU]") || message.contains("[YES]")) {
                    setChatPromtId(message.getText().toString());
                }
            }
        }
    }

    //{"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile 4fa1228c-8dd6-47c4-8fe3-b04b580311b8"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bHype_the_Time§e's profile"}},"text":"§9Party §8> §b[MVP§2+§b] Hype_the_Time§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"h:test"}],"text":""}// {"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile f772b2c7-bd2a-46e1-b1a2-41fa561157d6"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bShourtu§e's profile"}},"text":"§9Party §8> §b[MVP§c+§b] Shourtu§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"Hype_the_Time TEST"}],"text":""}
    //{"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile 4fa1228c-8dd6-47c4-8fe3-b04b580311b8"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bHype_the_Time§e's profile"}},"text":"§9Party §8> §b[MVP§2+§b] Hype_the_Time§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"h:test"}],"text":""}
    private final Map<String, Instant> partyDisbandedMap = new HashMap<>();
    private String lastPartyDisbandedUsername = null;


    public boolean isSpam(String message) {
        if (message.contains("Mana")) return true;
        if (message.contains("Status")) return true;
        if (message.contains("Achievement Points")) return true;
        return false;
    }

    public String test() {
        //put test code here
        sendNotification("test", "This is an example which was run of the h:test test");
        return new String();
    }

    public static void sendPrivateMessageToSelfError(String message) {
        sendPrivateMessageToSelfBase(Formatting.RED + message);
    }

    public static void sendPrivateMessageToSelfFatal(String message) {
        sendPrivateMessageToSelfBase(Formatting.DARK_RED + message);
    }

    public static void sendPrivateMessageToSelfSuccess(String message) {
        sendPrivateMessageToSelfBase(Formatting.GREEN + message);
    }

    public static void sendPrivateMessageToSelfInfo(String message) {
        sendPrivateMessageToSelfBase(Formatting.YELLOW + message);
    }

    public static void sendPrivateMessageToSelfImportantInfo(String message) {
        sendPrivateMessageToSelfBase(Formatting.GOLD + message);
    }

    public static void sendPrivateMessageToSelfDebug(String message) {
        sendPrivateMessageToSelfBase(Formatting.AQUA + message);
    }

    private static void sendPrivateMessageToSelfBase(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.of(Formatting.RED + message));
        }
    }

    public static void sendPrivateMessageToSelfText(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(message);
        }
    }

    public static void sendCommand(String s) {
        getConfig().sender.addSendTask(s);
    }

    public void sendNotification(String title, String text) {
        executionService.execute(() -> {
            try {
                InputStream inputStream = getClass().getResourceAsStream("/sounds/mixkit-sci-fi-confirmation-914.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                Thread.sleep(clip.getMicrosecondLength() / 1000);
                clip.close();
                audioInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        List<String> argsList = new ArrayList<>();
        argsList.add("--title");
        argsList.add(title);
        argsList.add("--passivepopup");
        argsList.add(text);
        argsList.add("5");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("kdialog");
            processBuilder.command().addAll(argsList);

            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Text createClientSideTellraw(String tellrawInput) {
        Text formattedMessage = null;
        try {
            formattedMessage = Text.Serializer.fromJson(tellrawInput);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid Json: \n" + tellrawInput);
        }
        return formattedMessage;
    }

    public static void followMenu(String menu, String message) {
        // Check the "menu" argument and execute the appropriate logic
        String command;
        String username = message.split(" ", 1)[0];
        if (message.contains(":")) {
            message = message.split(":", 2)[1].trim();
            if (menu.equalsIgnoreCase("pcm")) {
                command = "[\"\",{\"text\":\"@@username\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/pc @username\"}},{\"text\":\" [Copy_Message]\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"Copy-Text-Message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Copy the message the Player send without their name into the clipboard.\"]}},{\"text\":\" [Kick_Player]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p kick @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Kick the player from the party\"]}},{\"text\":\" [Promote_Player]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p promote @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Promote the player\"]}},{\"text\":\" [Demote_Player]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p demote @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Demote the player\"]}},{\"text\":\" [Transfer_to_Player]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p transfer @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Transfer the Party to the player\"]}},{\"text\":\" [Mute/Unmute_Party]\",\"color\":\"dark_aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p mute @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Mutes the ENTIRE party but party moderators or Hypixel Staff can still type.\"]}}]";
                //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"suggest_command","click_event_value":"/pc @username","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"@username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"Copy-Text-Message","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Copy the message the Player send without their name into the clipboard."}],"text":" [Copy_Message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"run_command","click_event_value":"/p kick @username","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Kick the player from the party"}],"text":" [Kick_Player]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_green","insertion":"","click_event_type":"run_command","click_event_value":"/p promote @username","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Promote the player"}],"text":" [Promote_Player]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"run_command","click_event_value":"/p demote @username","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Demote the player"}],"text":" [Demote_Player]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p transfer @username","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Transfer the Party to the player"}],"text":" [Transfer_to_Player]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_aqua","insertion":"","click_event_type":"run_command","click_event_value":"/p mute @username","hover_event_type":"show_text","hover_event_value":"","hover_event_object":{},"hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_object":{},"hover_event_children":[],"text":"Mutes the ENTIRE party but party moderators or Hypixel Staff can still type."}],"text":" [Mute/Unmute_Party]"}],"command":"%s","jtemplate":"tellraw"}
            }
            else if (menu.equalsIgnoreCase("sbacm")) {
                command = "[\"\",\"\\n\",{\"text\":\"@@username\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@username\"}},{\"text\":\" [Party_Player]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Invite the player to the party\"]}},{\"text\":\" [Ignore_Player]\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Add the player to your ignore list.\"]}},{\"text\":\" [Chat_Report_Player]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/creport @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Chat report the user with /creport.\"]}},{\"text\":\" [Visit_Player]\",\"color\":\"dark_green\",\"insertion\":\" \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/visit @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[]}},{\"text\":\" [/Invite_Player]\",\"color\":\"green\",\"insertion\":\" \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/invite @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"/invite the player to visit your Island / Garden.\"]}},{\"text\":\" [Copy_Message] \",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"Copy-Text-Message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Copy the message the user send without their prefixes nor their username\"]}},{\"text\":\" [Copy_Username]\",\"color\":\"dark_aqua\",\"insertion\":\" \",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"@username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Copy the players username into the clipboard.\"]}},{\"text\":\" [/msg_Chat_Player]\",\"color\":\"light_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/msg @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"/msg the Player. the chat will be set to message them. This means you do not need to type /msg upfront. To return to normal do /chat a\"]}},{\"text\":\" [Sky_shiiyu_player]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://sky.shiiyu.moe/stats/@username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Opens the players Skyblock Profile in Sky Crypt (sky.shiiyu.moe)\"]}},\"\\n\"]";
            }
            else if (menu.equalsIgnoreCase("acm")) {
                command = "[\"\",\"\\n\",{\"text\":\"@@username\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"@username\"}},{\"text\":\" [Party_Player]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Invite the player to the party\"]}},{\"text\":\" [Ignore_Player]\",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Add the player to your ignore list.\"]}},{\"text\":\" [Chat_Report_Player]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/creport @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Chat report the user with /creport.\"]}},{\"text\":\" [Copy_Message] \",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"Copy-Text-Message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Copy the message the user send without their prefixes nor their username\"]}},{\"text\":\" [Copy_Username]\",\"color\":\"dark_aqua\",\"insertion\":\" \",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"@username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Copy the players username into the clipboard.\"]}},{\"text\":\" [/msg_Chat_Player]\",\"color\":\"light_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/msg @username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"/msg the Player. the chat will be set to message them. This means you do not need to type /msg upfront. To return to normal do /chat a\"]}}";
            }
            else {
                // Handle unrecognized menu argument
                sendPrivateMessageToSelfError("Unrecognized menu argument! Do not use this command unless you know exactly what you are doing aka only use it as a developer!");
                return;
            }
            command = command.replaceAll("@username", username);
            command = command.replaceAll("Copy-Text-Message", message);
            sendPrivateMessageToSelfText(createClientSideTellraw(command));
        }
        else {
            sendPrivateMessageToSelfError("Invalid message!: " + message);
        }
    }

    public static void setChatPromtId(String logMessage) {
        String cbUUIDPattern = "/cb ([a-fA-F0-9-]+)";
        Pattern cbPattern = Pattern.compile(cbUUIDPattern);
        Matcher cbMatcher = cbPattern.matcher(logMessage);

        String yesClickAction = "/chatprompt ([a-fA-F0-9-]+) YES";
        Pattern yesPattern = Pattern.compile(yesClickAction);
        Matcher yesMatcher = yesPattern.matcher(logMessage);
        String lastPrompt = null;
        if (cbMatcher.find()) {
            lastPrompt = cbMatcher.group(1);
            String finalLastPrompt1 = lastPrompt;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String promptCommand = "/cb " + finalLastPrompt1;
                    BBsentials.getConfig().setLastChatPromptAnswer(promptCommand);
                    if (config.isDevModeEnabled()) {
                        Chat.sendPrivateMessageToSelfDebug("set the last prompt action too + \"" + promptCommand + "\"");
                    }
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    BBsentials.getConfig().setLastChatPromptAnswer(null);
                    return;
                }
            }).start();
        }
        if (yesMatcher.find()) {
            lastPrompt = yesMatcher.group(1);
            String finalLastPrompt = lastPrompt;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String promptCommand = "/chatprompt " + finalLastPrompt + " YES";
                    getConfig().setLastChatPromptAnswer(promptCommand);
                    if (config.isDevModeEnabled()) {
                        Chat.sendPrivateMessageToSelfDebug("set the last prompt action too + \"" + promptCommand + "\"");
                    }
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    getConfig().setLastChatPromptAnswer(null);
                    return;
                }
            }).start();

        }
    }

    public static Text replaceAllForText(Text input, String replace, String replaceWith) {
        String text = Text.Serializer.toJson(input);
        if (text.contains(replace)) {
            text = text.replaceAll("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "");
        }
        text = text.replace(replace, replaceWith);
        Text output = Text.Serializer.fromJson(text);
        return output;
    }
}
