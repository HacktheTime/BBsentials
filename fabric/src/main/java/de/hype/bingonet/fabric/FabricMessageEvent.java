package de.hype.bingonet.fabric;

import de.hype.bingonet.client.common.chat.Message;
import de.hype.bingonet.client.common.chat.MessageEvent;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.IChatHudDataAccess;
import net.minecraft.client.MinecraftClient;

public class FabricMessageEvent extends MessageEvent {
    public FabricMessageEvent(Message message) {
        super(message);
    }

    @Override
    public void deleteFromChat(int lineAmount) {
        try {
            IChatHudDataAccess chatHud = (IChatHudDataAccess) MinecraftClient.getInstance().inGameHud.getChatHud();
            chatHud.BingoNet$removeBottomLines(lineAmount);
        } catch (Exception e) {

        }
    }
}
