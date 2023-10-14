package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.mclibraries.MCChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeChat implements MCChat {
    public ForgeChat() {
        init();
    }
    public Chat chat = new Chat();
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        Message newMessage = chat.onEvent(new Message(IChatComponent.Serializer.componentToJson(event.message),event.message.getUnformattedText()));
        if (!newMessage.equals(event.message)){
            event.setCanceled(true);
            Chat.sendPrivateMessageToSelfText(newMessage);
        }
    }
    public void sendClientSideMessage(Message message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            player.addChatMessage(IChatComponent.Serializer.jsonToComponent(message.getJson()));
        }
    }
    public void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }
}
