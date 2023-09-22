package de.hype.bbsentials.client;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.constants.enviromentShared.Islands;
import de.hype.bbsentials.packets.packets.SplashNotifyPacket;
import de.hype.bbsentials.packets.packets.SplashUpdatePacket;

import java.util.HashMap;
import java.util.Map;

public class SplashManager {
    public static Map<Integer, DisplaySplash> splashPool = new HashMap<>();

    public SplashManager() {

    }

    public static void addSplash(SplashNotifyPacket packet) {
        splashPool.put(packet.splashId, new DisplaySplash(packet));
    }

    public static void updateSplash(SplashUpdatePacket packet) {
        DisplaySplash splash = splashPool.get(packet.splashId);
        if (splash != null) {
            if (splash.alreadyDisplayed) {
                if (BBsentials.config.showSplashStatusUpdates) {
                    Chat.sendPrivateMessageToSelf(splash.hubType.getDisplayName() + " #" + splash.hub + " is " + packet.status);
                }
            }
            else {
                splashPool.remove(splash.splashId);
            }
        }
    }

    public static void display(int splashId) {
        SplashNotifyPacket splash = splashPool.get(splashId);
        if (splash == null) return;
        String where;
        if (splash.hubType.equals(Islands.DUNGEON_HUB)) {
            where = "§5DUNGEON HUB§6";
        }
        else {
            where = "Hub";
        }
        BBsentials.bbserver.splashHighlightItem("HUB #" + splash.hub, 20);
        Chat.sendPrivateMessageToSelf("§6" + splash.splasherUsername + " is Splashing in " + where + " #" + splash.hub + " at " + splash.location + ":" + splash.extraMessage);
    }

    private static class DisplaySplash extends SplashNotifyPacket {
        public boolean alreadyDisplayed;

        public DisplaySplash(SplashNotifyPacket packet) {
            super(packet.splashId, packet.hub, packet.splasherUsername, packet.location, packet.hubType, packet.extraMessage, packet.lessWaste);
            alreadyDisplayed = false;
        }
    }
}
