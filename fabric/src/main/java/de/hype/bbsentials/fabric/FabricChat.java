package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.chat.Message;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.Text;

public class FabricChat {
    public FabricChat() {
        init();
    }
    public Chat chat = new Chat();
    private void init() {
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
}
