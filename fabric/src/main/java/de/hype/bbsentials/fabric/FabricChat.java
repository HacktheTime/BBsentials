package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.MCChat;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FabricChat implements MCChat {
    public Chat chat = new Chat();

    public FabricChat() {
        init();
    }
    public void init() {
        // Register a callback for a custom message type
//        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
//            chat.onEvent(new Message(Text.Serializer.toJson(message), message.getString()));
//        });
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, isActionBar) -> {
            boolean block = (BBsentials.chat.processNotThreaded(new Message(Text.Serializer.toJson(message), message.getString()), isActionBar) == null);
            return !block;
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register(this::prepareOnEvent);

    }

    public Text prepareOnEvent(Text text, boolean actionbar) {
        String json = Text.Serializer.toJson(text);
        Message message = new Message(json, text.getString(), actionbar);
        if (!actionbar) {
            message = chat.onEvent(message, actionbar);
        }
        Text toReturn = Text.Serializer.fromJson(message.getJson());
        if (BBsentials.config.swapActionBarChat && !BBsentials.config.swapOnlyBBsentials) {
            if (!actionbar) {
                showActionBar(message);
            }
            else {
                sendClientSideMessage(message);
            }
            return null;
        }
        return toReturn;
    }

    public void sendClientSideMessage(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()));
        }
    }

    public void showActionBar(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            if (BBsentials.config.overwriteActionBar.isEmpty())
                message = Message.of(BBsentials.config.overwriteActionBar);
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()), true);
        }
    }

    public void sendChatMessage(String message) {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
