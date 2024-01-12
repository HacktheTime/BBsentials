package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.SplashData;
import de.hype.bbsentials.shared.packets.function.SplashNotifyPacket;
import de.hype.bbsentials.shared.packets.function.SplashUpdatePacket;

import java.util.HashMap;
import java.util.Map;

public class SplashManager {
    public static Map<Integer, DisplaySplash> splashPool = new HashMap<>();

    public static void addSplash(SplashNotifyPacket packet) {
        try {
            splashPool.put(packet.splash.splashId, new DisplaySplash(packet));
        } catch (Exception ignored) {
            //cant happen anyway
        }
    }

    public static void updateSplash(SplashUpdatePacket packet) {
        DisplaySplash splash = splashPool.get(packet.splashId);
        if (splash != null) {
            if (splash.alreadyDisplayed) {
                if (BBsentials.splashConfig.showSplashStatusUpdates) {
                    Chat.sendPrivateMessageToSelfImportantInfo(splash.hubType.getDisplayName() + " #" + splash.hubNumber + " is " + packet.status);
                }
            }
            else {
                splashPool.remove(splash.splashId);
            }
        }
    }

    public static void display(int splashId) {
        DisplaySplash splash = splashPool.get(splashId);
        if (splash == null) return;
        String where;
        if (splash.equals(Islands.DUNGEON_HUB)) {
            where = "§5DUNGEON HUB§6";
        }
        else {
            where = "Hub";
        }
        BBsentials.connection.splashHighlightItem(splash, 20);
        Chat.sendPrivateMessageToSelfImportantInfo(splash.announcer + " is Splashing in " + where + " #" + splash.hubNumber + " at " + splash.locationInHub + ":" + splash.extraMessage);
    }

    private static class DisplaySplash extends SplashData {
        public boolean alreadyDisplayed;

        public DisplaySplash(SplashNotifyPacket packet) throws Exception {
            super(packet.splash.announcer, packet.splash.splashId, packet.splash.hubNumber, packet.splash.locationInHub, packet.splash.hubType, packet.splash.extraMessage, packet.splash.lessWaste, packet.splash.status);
            alreadyDisplayed = false;
        }

        @Override
        public void statusUpdate(StatusConstants newStatus) {
            //ignored
        }
    }
}
