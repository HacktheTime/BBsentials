package de.hype.bbsentials.client;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.constants.enviromentShared.Islands;
import net.minecraft.client.MinecraftClient;

public class BBUtils {
    public static Islands getCurrentIsland() {
        try {
            String string = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-b").getDisplayName().getString();
            if (!string.startsWith("Area: ")) {
                Chat.sendPrivateMessageToSelf("§4Could not get Area data. Are you in Skyblock?");
            }
            else {
                return Islands.getByDisplayName(string.replace("Area: ", "").trim());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static int getPlayerCount() {
        return Integer.parseInt(MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!B-a").getDisplayName().getString().trim().replaceAll("[^0-9]", ""));
    }

    public static String getServer() {
        return MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-c").getDisplayName().getString().replace("Server:", "").trim();
    }

    public static boolean isOnMegaServer() {
        return getServer().toLowerCase().startsWith("mega");
    }

    public static boolean isOnMiniServer() {
        return getServer().toLowerCase().startsWith("mini");
    }

    public static int getMaximumPlayerCount() {
        boolean mega = isOnMegaServer();
        Islands island = getCurrentIsland();
        if (island == null) return 100;
        if (island.equals(Islands.HUB)) {
            if (mega) return 80;
            else return 24;
        }
        return 24;
    }
}
