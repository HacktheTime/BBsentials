package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.MessageEvent;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.MCChat;
import de.hype.bbsentials.shared.objects.Message;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public class FabricChat implements MCChat {
    public Chat chat = new Chat();
    MinecraftClient client;

    public FabricChat() {
        init();
    }

    public void init() {
        // Register a callback for a custom message type
//        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
//            chat.onEvent(new Message(Text.Serialization.toJsonString(message), message.getString()));
//        });
        client = MinecraftClient.getInstance();
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, isActionBar) -> {
            if (client.world == null) return true;
            de.hype.bbsentials.client.common.chat.Message bbMessage = new de.hype.bbsentials.client.common.chat.Message(FabricTextUtils.opposite(message), message.getString());
            boolean block = (BBsentials.chat.processNotThreaded(bbMessage, isActionBar) == null);
            MessageEvent event = new FabricMessageEvent(bbMessage);
            BBsentials.annotationProcessor.triggerEvent(event);
            if (event.isCanceled()) return !event.isCanceled();
            return !block;
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register(this::prepareOnEvent);

    }

    public Text prepareOnEvent(Text text, boolean actionbar) {
        if (MinecraftClient.getInstance().world == null) return text;
        String json = FabricTextUtils.opposite(text);
        Message message = new de.hype.bbsentials.client.common.chat.Message(json, text.getString(), actionbar);

        if (!actionbar) {
            message = chat.onEvent((de.hype.bbsentials.client.common.chat.Message) message, actionbar);
        }
        Text toReturn = null;
        try {
            toReturn = FabricTextUtils.opposite(message.getJson());
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError("Text: " + FabricTextUtils.opposite(text) + " Error: " + e.getMessage());
            e.printStackTrace();
        }

        if (BBsentials.funConfig.swapActionBarChat && !BBsentials.funConfig.swapOnlyBBsentials) {
            sendClientSideMessage(message, !actionbar);
        }
        if (actionbar) {
            Message finalMessage = message;
            BBsentials.executionService.execute(() -> {
                processActionbarThreaded(finalMessage);
            });
        }
        return toReturn;
    }

    private void processActionbarThreaded(Message message) {
    }

    @Override
    public void sendClientSideMessage(Message message, boolean actionbar) {
        MinecraftClient client = MinecraftClient.getInstance();
        Text text = FabricTextUtils.opposite(message.getJson());
        if (text == null) return;
        if (client.inGameHud != null) {
            if (actionbar) {
                if (BBsentials.funConfig.overwriteActionBar.isEmpty())
                    text = FabricTextUtils.opposite(BBsentials.funConfig.overwriteActionBar);
                client.inGameHud.setOverlayMessage(text, false);
            } else client.inGameHud.getChatHud().addMessage(text);
        }
    }

    @Override

    public void sendChatMessage(String message) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return;
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
}
