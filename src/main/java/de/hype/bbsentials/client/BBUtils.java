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
                return Islands.getByDisplayName(string.replace("Area: ","").trim());
            }
        } catch (Exception e) {
        }
        return null;
    }
}
