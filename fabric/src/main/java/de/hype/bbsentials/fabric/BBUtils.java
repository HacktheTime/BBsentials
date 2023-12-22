package de.hype.bbsentials.fabric;

import com.google.common.collect.Lists;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.shared.constants.EnumUtils;
import de.hype.bbsentials.shared.constants.Islands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Iterator;
import java.util.List;

public class BBUtils implements de.hype.bbsentials.client.common.mclibraries.BBUtils {
    public Islands getCurrentIsland() {
        try {
            String string = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-b").getDisplayName().getString();
            if (!string.startsWith("Area: ")) {
                Chat.sendPrivateMessageToSelfError("Could not get Area data. Are you in Skyblock?");
            }
            else {
                return EnumUtils.getEnumByName(Islands.class, string.replace("Area: ", "").trim());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public int getPlayerCount() {
        return Integer.parseInt(MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!B-a").getDisplayName().getString().trim().replaceAll("[^0-9]", ""));
    }

    public String getServer() {
        return MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-c").getDisplayName().getString().replace("Server:", "").trim();
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

    public long getLobbyTime() {
        return MinecraftClient.getInstance().world.getLevelProperties().getTimeOfDay();
    }
    public List<String> getPlayers() {
        List<String> list = Lists.newArrayList();
        Iterator var2 = MinecraftClient.getInstance().getNetworkHandler().getPlayerList().iterator();
        while (var2.hasNext()) {
            PlayerListEntry playerListEntry = (PlayerListEntry) var2.next();
            String playerName = playerListEntry.getProfile().getName();
            if (!playerName.startsWith("!")) {
                list.add(playerName);
            }
        }
        return list;
    }
}
