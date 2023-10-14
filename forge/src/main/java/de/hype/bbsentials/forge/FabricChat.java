package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.mclibraries.MCChat;
import de.hype.bbsentials.forge.chat.Chat;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FabricChat implements MCChat {
    public FabricChat() {
        init();
    }
    public Chat chat = new Chat();
    public void init() {
        // Register a callback for a custom message type
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            chat.onEvent(new Message(Text.Serializer.toJson(message),message.getString()));
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> (Text.Serializer.fromJson(chat.onEvent(new Message(Text.Serializer.toJson(message),message.getString())).getJson())));
        ClientSendMessageEvents.CHAT.register(message -> {
            if (message.startsWith("/")) {
                System.out.println("Sent command: " + message);
            }
        });
    }
    public void sendClientSideMessage(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()));
        }
    }
    public void sendChatMessage(String message) {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
