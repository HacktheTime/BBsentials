package de.hype.bbsentials.common.chat;

import de.hype.bbsentials.common.api.Formatting;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.client.Config;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.packets.packets.SplashUpdatePacket;

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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {

    //{"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile 4fa1228c-8dd6-47c4-8fe3-b04b580311b8"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bHype_the_Time§e's profile"}},"text":"§9Party §8> §b[MVP§2+§b] Hype_the_Time§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"h:test"}],"text":""}// {"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile f772b2c7-bd2a-46e1-b1a2-41fa561157d6"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bShourtu§e's profile"}},"text":"§9Party §8> §b[MVP§c+§b] Shourtu§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"Hype_the_Time TEST"}],"text":""}
    //{"strikethrough":false,"extra":[{"strikethrough":false,"clickEvent":{"action":"run_command","value":"/viewprofile 4fa1228c-8dd6-47c4-8fe3-b04b580311b8"},"hoverEvent":{"action":"show_text","contents":{"strikethrough":false,"text":"§eClick here to view §bHype_the_Time§e's profile"}},"text":"§9Party §8> §b[MVP§2+§b] Hype_the_Time§f: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"text":"h:test"}],"text":""}
    private final Map<String, Instant> partyDisbandedMap = new HashMap<>();
    private String lastPartyDisbandedUsername = null;

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
        EnvironmentCore.chat.sendClientSideMessage(Message.of(message), false);
    }

    public static void sendPrivateMessageToSelfText(Message message) {
        EnvironmentCore.chat.sendClientSideMessage(message);
    }

    public static void sendCommand(String s) {
        BBsentials.getConfig().sender.addSendTask(s);
    }

    public static void setChatPromtId(String logMessage) {
        String yesClickAction = "/chatprompt ([a-fA-F0-9-]+) YES";
        Pattern yesPattern = Pattern.compile(yesClickAction);
        Matcher yesMatcher = yesPattern.matcher(logMessage);
        String lastPrompt = null;
        if (yesMatcher.find()) {
            lastPrompt = yesMatcher.group(1);
            setChatCommand("/chatprompt " + lastPrompt + " YES", 10);
        }
    }

    /**
     * @param command          the command to be executed
     * @param timeBeforePerish in seconds before its reset to nothing
     */
    public static void setChatCommand(String command, int timeBeforePerish) {
        BBsentials.getConfig().setLastChatPromptAnswer(command);
        if (BBsentials.config.isDevModeEnabled()) {
            Chat.sendPrivateMessageToSelfDebug("set the last prompt action too + \"" + command + "\"");
        }
        BBsentials.executionService.schedule(() -> {
            BBsentials.getConfig().setLastChatPromptAnswer(null);
        }, 10, TimeUnit.SECONDS);
    }

    public Message onEvent(Message text, boolean actionbar) {
        if (!isSpam(text.getString())) {
            if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
                System.out.println("got a message: " + text.getJson());
            }
            BBsentials.executionService.execute(() -> processThreaded(text));
            return processNotThreaded(text, actionbar);
        }
        return text; // Return the original message if it is spam
    }

    //Handle in the messages which need to be modified here
    public Message processNotThreaded(Message message, boolean actionbar) {
//        if (message.isFromParty()) {
//           message.replaceInJson("\"action\":\"run_command\",\"value\":\"/viewprofile", "\"action\":\"run_command\",\"value\":\"/bviewprofile " + messageUnformatted.split(">", 1)[1].trim());
//        }
        if (actionbar && !BBsentials.config.overwriteActionBar.isEmpty()) {
            if (message.getUnformattedString().equals(BBsentials.config.overwriteActionBar.replaceAll("§.", ""))) {
                return message;
            }
            return null;
        }
        if (message.isFromReportedUser()) {
            sendPrivateMessageToSelfBase(Formatting.RED + "B: " + message.getUnformattedString());
            return null;
        }
        if (BBsentials.config.doPartyChatCustomMenu && message.isFromParty()) {
            message.replaceInJson("/viewprofile \\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "/socialoptions party " + message.getPlayerName() + " " + message.getUnformattedString());
        }
        else if (BBsentials.config.doGuildChatCustomMenu && message.isFromGuild()) {
            message.replaceInJson("/viewprofile \\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "/socialoptions guild " + message.getPlayerName() + " " + message.getUnformattedString());
        }
        else if (BBsentials.config.doAllChatCustomMenu) {
            message.replaceInJson("/socialoptions " + message.getPlayerName(), "/socialoptions sb " + message.getPlayerName() + " " + message.getUnformattedString());
        }

        return message;
    }

    public void processThreaded(Message message) {
        if (message.getString() != null) {
            String messageUnformatted = message.getUnformattedString();
            String username = message.getPlayerName();
            if (message.isFromReportedUser()) {

            }
            else if (!EnvironmentCore.mcUtils.isWindowFocused()) {
                if (BBsentials.config.doDesktopNotifications) {
                    if ((messageUnformatted.endsWith("is visiting Your Garden !") || messageUnformatted.endsWith("is visiting Your Island !")) && !EnvironmentCore.mcUtils.isWindowFocused() && BBsentials.config.doDesktopNotifications) {
                        sendNotification("BBsentials Visit-Watcher", messageUnformatted);
                    }
                    else if (message.isMsg()) {
                        sendNotification("BBsentials Message Notifier", username + " sent you the following message: " + message.getMessageContent());
                    }
                    if (message.getMessageContent().toLowerCase().contains(BBsentials.getConfig().getUsername().toLowerCase()) || (message.getMessageContent().toLowerCase().contains(BBsentials.getConfig().getNickname().toLowerCase() + " ") && BBsentials.getConfig().getNotifForParty().toLowerCase().equals("nick")) || BBsentials.getConfig().getNotifForParty().toLowerCase().equals("all")) {
                        sendNotification("BBsentials Party Chat Notification", username + " : " + message.getMessageContent());
                    }
                    else {
                        if (message.getMessageContent().toLowerCase().contains(BBsentials.getConfig().getUsername().toLowerCase()) || message.getMessageContent().toLowerCase().contains(BBsentials.config.getNickname().toLowerCase() + " ")) {
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
                        if (BBsentials.config.acceptReparty) {
                            if (lastDisbandedInstant != null && lastDisbandedInstant.isAfter(Instant.now().minusSeconds(20)) && (username.equals(lastPartyDisbandedUsername))) {
                                sendCommand("/p accept " + username);
                            }
                        }
                    }
                    if (!EnvironmentCore.mcUtils.isWindowFocused()) {
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
                else if ((message.contains("Party Leader:") && !message.contains(BBsentials.getConfig().getUsername())) || message.equals("You are not currently in a party.") || (message.contains("warped the party into a Skyblock Dungeon") && !message.startsWith(BBsentials.getConfig().getUsername()) || (!message.startsWith("The party was transferred to " + BBsentials.getConfig().getUsername()) && message.startsWith("The party was transferred to"))) || messageUnformatted.endsWith(BBsentials.getConfig().getUsername() + " is now a Party Moderator") || (message.startsWith("The party was disbanded")) || (message.contains("You have joined ") && message.contains("'s party!")) || (message.contains("Party Leader, ") && message.contains(" , summoned you to their server.")) || (message.contains("warped to your dungeon"))) {
                    BBsentials.getConfig().setIsLeader(false);
                    if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
                        sendPrivateMessageToSelfDebug("Leader: " + BBsentials.getConfig().isPartyLeader());
                    }
                }
                else if (BBsentials.config.getPlayersInParty().length == 0 && messageUnformatted.endsWith("to the party! They have 60 seconds to accept")) {
                    BBsentials.config.setIsLeader(true);
                }
                else if (messageUnformatted.startsWith("You'll be partying with:")) {
                    List<String> members = new ArrayList<>();
                    for (String users : messageUnformatted.replace("You'll be partying with:", "").replaceAll("\\[[^\\]]*\\]", "").trim().split(",")) {
                        if (users.contains("and ")) {
                            break;
                        }
                        members.add(users);
                    }
                    Config.partyMembers = members;
                }
                else if (((messageUnformatted.startsWith("Party Leader: ") && messageUnformatted.endsWith(BBsentials.getConfig().getUsername() + " ●"))) || (message.contains(BBsentials.getConfig().getUsername() + " warped the party to a SkyBlock dungeon!")) || message.startsWith("The party was transferred to " + BBsentials.getConfig().getUsername()) || message.getUnformattedString().endsWith(" has promoted " + BBsentials.getConfig().getUsername() + " to Party Leader") || (message.contains("warped to your dungeon"))) {
                    BBsentials.getConfig().setIsLeader(true);
                    if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
                        sendPrivateMessageToSelfDebug("Leader: " + BBsentials.getConfig().isPartyLeader());
                    }
                }
                else if (message.getUnformattedString().equals("Please type /report confirm to log your report for staff review.")) {
                    sendCommand("/report confirm");
                }
                else if (messageUnformatted.startsWith("BUFF! You splashed yourself with")) {
                    if (BBsentials.splashStatusUpdateListener != null) {
                        BBsentials.splashStatusUpdateListener.setStatus(SplashUpdatePacket.STATUS_SPLASHING);
                    }
                }
                else if (messageUnformatted.equals("Click here to purchase a new 6 hour pass for 10,000 Coins")) {
                    setChatCommand("/purchasecrystallhollowspass", 30);
                }
            }

            else if (message.isFromGuild()) {

            }
            else if (message.isFromParty()) {
                if (message.getMessageContent().equals("warp") && BBsentials.config.isPartyLeader()) {
                    if (BBsentials.config.getPlayersInParty().length == 1) {
                        Chat.sendCommand("/p warp");
                    }
                    else if (BBsentials.config.getPlayersInParty().length >= 10) {
                        //ignored because soo many players
                    }
                    else if (BBsentials.config.getPlayersInParty().length > 1) {
                        Chat.sendPrivateMessageToSelfText(Message.tellraw("[\"\",{\"text\":\"@username\",\"color\":\"red\"},\" \",\"is requesting a warp. Press \",{\"keybind\":\"Chat Prompt Yes / Open Menu\",\"color\":\"green\"},\" to warp the entire \",{\"text\":\"Party\",\"color\":\"gold\"},\".\"]"));
                        setChatCommand("/p warp", 10);
                    }
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
                    setChatPromtId(message.getJson());
                }
            }
        }
    }

    public boolean isSpam(String message) {
        if (message == null) return true;
        if (message.isEmpty()) return true;
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

    public void sendNotification(String title, String text) {
        sendNotification(title, text, 1);
    }

    public void sendNotification(String title, String text, float volume) {
        BBsentials.executionService.execute(() -> {
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
}
