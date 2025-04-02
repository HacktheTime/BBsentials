package de.hype.bingonet.client.common.client;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.config.PartyConfig;
import de.hype.bingonet.client.common.objects.ChatPrompt;
import de.hype.bingonet.shared.constants.Formatting;
import de.hype.bingonet.shared.constants.Islands;
import de.hype.bingonet.shared.objects.Message;
import de.hype.bingonet.shared.objects.SplashData;
import de.hype.bingonet.shared.packets.function.RequestDynamicSplashInvitePacket;
import de.hype.bingonet.shared.packets.function.SplashNotifyPacket;
import de.hype.bingonet.shared.packets.function.SplashUpdatePacket;
import org.apache.commons.text.StringEscapeUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SplashManager {
    public static Map<Integer, DisplaySplash> splashPool = new HashMap<>();

    public static void addSplash(SplashData splash) {
        try {
            splashPool.put(splash.splashId, new DisplaySplash(splash));
            BingoNet.executionService.schedule(() -> splashPool.remove(splash.splashId), 5, TimeUnit.MINUTES);
        } catch (Exception ignored) {
            //cant happen anyway
        }
    }

    public static void updateSplash(SplashUpdatePacket packet) {
        DisplaySplash splash = splashPool.get(packet.splashId);
        if (splash != null) {
            if (splash.alreadyDisplayed) {
                if (BingoNet.splashConfig.showSplashStatusUpdates && splash.hubSelectorData != null) {
                    Chat.sendPrivateMessageToSelfImportantInfo(splash.hubSelectorData.hubType.getDisplayName() + " #" + splash.hubSelectorData.hubNumber + " is " + packet.status);
                }
            } else {
                splashPool.remove(splash.splashId);
            }
        }
    }

    public static void display(int splashId) {
        DisplaySplash splash = splashPool.get(splashId);
        if (splash == null) return;
        String tellraw;
        if (splash.hubSelectorData == null) {
            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"light_purple","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@splasher "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"is splashing in a Private Server. "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Press ("},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"keybind":"Chat Prompt Yes / Open Menu"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":") to request an invite. "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Location: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@location "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"|"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" @extramessage"}],"command":"%s","jtemplate":"tellraw"}
            tellraw = "[{\"text\":\"\"},{\"text\":\"@splasher \",\"color\":\"light_purple\"},{\"text\":\"is splashing in a Private Server. \"},{\"text\":\"Press (\"},{\"keybind\":\"Chat Prompt Yes / Open Menu\",\"color\":\"green\"},{\"text\":\") to request an invite. \"},{\"text\":\"Location: \"},{\"text\":\"@location \",\"color\":\"green\"},{\"text\":\"|\"},{\"text\":\" @extramessage\",\"color\":\"gold\"}]";
            BingoNet.temporaryConfig.lastChatPromptAnswer = new ChatPrompt(() -> {
                try {
                    BingoNet.connection.sendPacket(new RequestDynamicSplashInvitePacket(splashId));
                    BingoNet.partyConfig.partyAcceptConfig.put(splash.announcer, new PartyConfig.AcceptPartyInvites(true, Instant.now().plus(5, ChronoUnit.MINUTES)));
                    Chat.sendPrivateMessageToSelfInfo("Request sent.");
                } catch (Exception e) {
                    Chat.sendPrivateMessageToSelfError(e.getMessage());
                }
            }, 5);
        } else {
            String islandType;
            if (splash.hubSelectorData.hubType.equals(Islands.DUNGEON_HUB)) {
                islandType = "%sDUNGEON HUB%s".formatted(Formatting.LIGHT_PURPLE, Formatting.WHITE);
            } else {
                islandType = "Hub";
            }
            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"light_purple","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@splasher "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"is splashing in "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@island "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"#"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@hubnumber"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" Press ("},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"keybind":"Chat Prompt Yes / Open Menu"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":") to warp to @island "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"at Location: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@location "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"| "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@extramessage"}],"command":"%s","jtemplate":"tellraw"}
            tellraw = "[{\"text\":\"\"},{\"text\":\"@splasher \",\"color\":\"light_purple\"},{\"text\":\"is splashing in \"},{\"text\":\"@island \"},{\"text\":\"#\"},{\"text\":\"@hubnumber\",\"color\":\"gold\"},{\"text\":\" Press (\"},{\"keybind\":\"Chat Prompt Yes / Open Menu\",\"color\":\"green\"},{\"text\":\") to warp to @island \"},{\"text\":\"at Location: \"},{\"text\":\"@location \",\"color\":\"green\"},{\"text\":\"| \"},{\"text\":\"@extramessage\",\"color\":\"gold\"}]";
            BingoNet.temporaryConfig.lastChatPromptAnswer = new ChatPrompt(() -> {
                BingoNet.sender.addSendTask("/warp " + splash.hubSelectorData.hubType.getWarpArgument(), 0);
                if (splash.hubSelectorData.hubType.equals(Islands.HUB)) {
                    Islands currentIsland = BingoNet.dataStorage.getIsland();
                    switch (currentIsland) {
                        case GOLD_MINE, The_Park, SPIDERS_DEN -> {
                            BingoNet.sender.addSendTask("/warp " + splash.hubSelectorData.hubType.getWarpArgument(), 3);
                        }
                        default -> {

                        }
                    }
                }
            }, 10);
            islandType = StringEscapeUtils.escapeJson(islandType);
            tellraw = tellraw.replace("@island", islandType).replace("@hubnumber", String.valueOf(splash.hubSelectorData.hubNumber));
        }
        tellraw = tellraw.replace("@splasher", StringEscapeUtils.escapeJson(splash.announcer));
        tellraw = tellraw.replace("@location", StringEscapeUtils.escapeJson(splash.locationInHub.getDisplayString()));
        tellraw = tellraw.replace("@extramessage", splash.extraMessage != null ? StringEscapeUtils.escapeJson(splash.extraMessage) : "");
        Chat.sendPrivateMessageToSelfText(Message.tellraw(tellraw));
    }

    public static class DisplaySplash extends SplashData {
        public boolean alreadyDisplayed;
        public Instant receivedTime = Instant.now();

        public DisplaySplash(SplashData packet) {
            super(packet);
            alreadyDisplayed = false;
        }

        public Instant getReceivedTime() {
            return receivedTime;
        }
    }
}
