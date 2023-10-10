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
            sendPrivateMessageToSelfBase(Formatting.RED + "B: " + message.getUnformattedString());
            return null;
        }
        if (config.doPartyChatCustomMenu && message.isFromParty()) {
            message.replaceInJson("/viewprofile \\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "/socialoptions party " + message.getPlayerName() + " " + message.getUnformattedString());
        }
        else if (config.doGuildChatCustomMenu && message.isFromGuild()) {
            message.replaceInJson("/viewprofile \\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "/socialoptions guild " + message.getPlayerName() + " " + message.getUnformattedString());
        }
        else if (config.doAllChatCustomMenu) {
            System.out.println("User: '" + message.getPlayerName() + "' | Message: " + message.getUnformattedString());
            message.replaceInJson("/socialoptions " + message.getPlayerName(), "/socialoptions sb " + message.getPlayerName() + " " + message.getUnformattedString());
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
                    if (message.getMessageContent().toLowerCase().contains(getConfig().getUsername().toLowerCase()) || (message.getMessageContent().toLowerCase().contains(getConfig().getNickname().toLowerCase() + " ") && getConfig().getNotifForParty().toLowerCase().equals("nick")) || getConfig().getNotifForParty().toLowerCase().equals("all")) {
                        sendNotification("BBsentials Party Chat Notification", username + " : " + message.getMessageContent());
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
                else if ((message.contains("Party Leader:") && !message.contains(getConfig().getUsername())) || message.equals("You are not currently in a party.") || (message.contains("warped the party into a Skyblock Dungeon") && !message.startsWith(getConfig().getUsername()) || (!message.startsWith("The party was transferred to " + getConfig().getUsername()) && message.startsWith("The party was transferred to"))) || messageUnformatted.endsWith(getConfig().getUsername() + " is now a Party Moderator") || (message.startsWith("The party was disbanded")) || (message.contains("You have joined ") && message.contains("'s party!")) || (message.contains("Party Leader, ") && message.contains(" , summoned you to their server.")) || (message.contains("warped to your dungeon"))) {
                    BBsentials.getConfig().setIsLeader(false);
                    if (getConfig().isDetailedDevModeEnabled()) {
                        sendPrivateMessageToSelfDebug("Leader: " + getConfig().isLeader());
                    }
                }
                else if (config.getPlayersInParty().length == 0 && messageUnformatted.endsWith("to the party! They have 60 seconds to accept")) {
                    config.setIsLeader(true);
                }
                else if (messageUnformatted.startsWith("You'll be partying with:")) {
                    List<String> members = new ArrayList<>();
                    for (String users : messageUnformatted.replace("You'll be partying with:", "").replaceAll("\\[[^\\]]*\\]","").trim().split(",")) {
                        if (users.contains("and ")){break;}
                        members.add(users);
                    }
                    Config.partyMembers=members;
                }
                else if (((messageUnformatted.startsWith("Party Leader: ") && messageUnformatted.endsWith(getConfig().getUsername() + " ●"))) || (message.contains(getConfig().getUsername() + " warped the party to a SkyBlock dungeon!")) || message.startsWith("The party was transferred to " + getConfig().getUsername()) || message.getUnformattedString().endsWith(" has promoted " + getConfig().getUsername() + " to Party Leader") || (message.contains("warped to your dungeon"))) {
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
