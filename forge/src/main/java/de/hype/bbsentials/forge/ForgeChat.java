package de.hype.bingonet.forge;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.chat.Message;
import de.hype.bingonet.client.common.mclibraries.MCChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeChat implements MCChat {
    public Chat chat = new Chat();

    public ForgeChat() {
        init();
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (!(event.type == 2)) {
            //2 means action bar
//            Chat.sendPrivateMessageToSelfDebug(String.valueOf(((int) event.type)));
            Message message = new Message(IChatComponent.Serializer.componentToJson(event.message), event.message.getUnformattedText());
            Message newMessage = chat.onEvent(message, false);
            if (!newMessage.equals(message)) {
                event.setCanceled(true);
                sendClientSideMessage(newMessage);
            }
        }
    }

    public void sendClientSideMessage(Message message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            String json = message.getJson();
            IChatComponent text = IChatComponent.Serializer.jsonToComponent(json);
            player.addChatMessage(text);
        }
    }

    public void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }

    public void showActionBar(Message message) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            String json = message.getJson();
            IChatComponent text = IChatComponent.Serializer.jsonToComponent(json);
            // Display the text in the action bar
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(text);
        }
    }

}
