package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.MCChat;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public class FabricChat implements MCChat {
    public Chat chat = new Chat();

    public FabricChat() {
        init();
    }

    public void init() {
        // Register a callback for a custom message type
//        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
//            chat.onEvent(new Message(Text.Serialization.toJsonString(message), message.getString()));
//        });
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, isActionBar) -> {
            boolean block = (BBsentials.chat.processNotThreaded(new Message(FabricTextUtils.opposite(message), message.getString()), isActionBar) == null);
            return !block;
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register(this::prepareOnEvent);

    }

    public Text prepareOnEvent(Text text, boolean actionbar) {
        String json = FabricTextUtils.opposite(text);
        Message message = new Message(json, text.getString(), actionbar);

        if (!actionbar) {
            message = chat.onEvent(message, actionbar);
        }
        Text toReturn = null;
        try {
            toReturn = FabricTextUtils.opposite(message.getJson());
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError("Text: " + FabricTextUtils.opposite(text) + " Error: " + e.getMessage());
            e.printStackTrace();
        }

        if (BBsentials.funConfig.swapActionBarChat && !BBsentials.funConfig.swapOnlyBBsentials) {
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
            client.player.sendMessage(FabricTextUtils.opposite(message.getJson()));
        }
    }

    public void showActionBar(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            if (BBsentials.funConfig.overwriteActionBar.isEmpty())
                message = Message.of(BBsentials.funConfig.overwriteActionBar);
            client.player.sendMessage(FabricTextUtils.opposite(message.getJson()), true);
        }
    }

    public void sendChatMessage(String message) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
