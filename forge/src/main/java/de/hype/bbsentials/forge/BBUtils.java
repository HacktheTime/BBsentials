package de.hype.bbsentials.forge;

import com.google.common.collect.Lists;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.shared.constants.EnumUtils;
import de.hype.bbsentials.shared.constants.Islands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.Iterator;
import java.util.List;

public class BBUtils implements de.hype.bbsentials.client.common.mclibraries.BBUtils {
    public Islands getCurrentIsland() {
        try {
            String string = getTabListPlayerName("!C-b");
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

    public static String getTabListPlayerName(String id) {
        return Minecraft.getMinecraft().getNetHandler().getPlayerInfo(id).getDisplayName().getUnformattedText();
    }
    public int getPlayerCount() {
        return Integer.parseInt(getTabListPlayerName("!B-a").trim().replaceAll("[^0-9]", ""));
    }

    public String getServer() {
        return getTabListPlayerName("!C-c").replace("Server:", "").trim();
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
        Iterator var2 = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().iterator();
        while (var2.hasNext()) {
            NetworkPlayerInfo playerListEntry = (NetworkPlayerInfo) var2.next();
            String playerName = playerListEntry.getDisplayName().getUnformattedText();
            if (!playerName.startsWith("!")) {
                list.add(playerName);
            }
        }
        return list;
    }
}
