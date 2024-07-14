package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.chat.MessageEvent;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IChatHudDataAccess;
import net.minecraft.client.MinecraftClient;

public class FabricMessageEvent extends MessageEvent {
    public FabricMessageEvent(Message message) {
        super(message);
    }

    @Override
    public void deleteFromChat(int lineAmount) {
        try {
            IChatHudDataAccess chatHud = (IChatHudDataAccess) MinecraftClient.getInstance().inGameHud.getChatHud();
            chatHud.BBsentials$removeBottomLines(lineAmount);
        } catch (Exception e) {

        }
    }
}
