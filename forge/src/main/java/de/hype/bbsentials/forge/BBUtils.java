package de.hype.bbsentials.forge;

import com.google.common.collect.Lists;
import de.hype.bbsentials.common.constants.enviromentShared.Islands;

import java.util.List;

public class BBUtils implements de.hype.bbsentials.common.mclibraries.BBUtils {
    public Islands getCurrentIsland() {
//        try {
//            String string = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-b").getDisplayName().getString();
//            if (!string.startsWith("Area: ")) {
//                Chat.sendPrivateMessageToSelfError("Could not get Area data. Are you in Skyblock?");
//            }
//            else {
//                return Islands.getByDisplayName(string.replace("Area: ", "").trim());
//            }
//        } catch (Exception e) {
//        }
//        return null;
        return Islands.PRIVATE_ISLAND;
    }

    public int getPlayerCount() {
//        return Integer.parseInt(MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!B-a").getDisplayName().getString().trim().replaceAll("[^0-9]", ""));
        return 0;
    }

    public String getServer() {
//        return MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-c").getDisplayName().getString().replace("Server:", "").trim();
        return "mini0b";
    }

    public boolean isOnMegaServer() {
        return getServer().toLowerCase().startsWith("mega");
    }

    public boolean isOnMiniServer() {
        return getServer().toLowerCase().startsWith("mini");
    }

    public int getMaximumPlayerCount() {
        boolean mega = isOnMegaServer();
        Islands island = getCurrentIsland();
        if (island == null) return 100;
        if (island.equals(Islands.HUB)) {
            if (mega) return 80;
            else return 24;
        }
        return 24;
    }

    public List<String> getPlayers() {
        List<String> list = Lists.newArrayList();
//        Iterator var2 = MinecraftClient.getInstance().getNetworkHandler().getPlayerList().iterator();
//        while (var2.hasNext()) {
//            PlayerListEntry playerListEntry = (PlayerListEntry) var2.next();
//            String playerName = playerListEntry.getProfile().getName();
//            if (!playerName.startsWith("!")) {
//                list.add(playerName);
//            }
//        }
        return list;
    }
}
