package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.mclibraries.MCChat;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FabricChat implements MCChat {
    public Chat chat = new Chat();

    public FabricChat() {
        init();
    }

    public void init() {
        // Register a callback for a custom message type
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            chat.onEvent(new Message(Text.Serializer.toJson(message), message.getString()));
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, actionbar) -> {
            if (!actionbar) {
                return prepareOnEvent(message, actionbar);
            }
            return message;
        });
        ClientSendMessageEvents.CHAT.register(message -> {
            if (message.startsWith("/")) {
                System.out.println("Sent command: " + message);
            }
        });
    }

    public Text prepareOnEvent(Text text, boolean actionbar) {
        String json = Text.Serializer.toJson(text);
        Message message = new Message(json, text.getString(), actionbar);
        Message returned = chat.onEvent(message);
        Text toReturn = Text.Serializer.fromJson(returned.getJson());
        return toReturn;
    }

    public void sendClientSideMessage(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()));
        }
    }

    public void sendClientSideMessage(Message message, boolean actionbar) {
        if (actionbar) {
            showActionBar(message);
        }
        else {
            sendClientSideMessage(message);
        }
    }

    public void showActionBar(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()), true);
        }
    }

    public void sendChatMessage(String message) {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
