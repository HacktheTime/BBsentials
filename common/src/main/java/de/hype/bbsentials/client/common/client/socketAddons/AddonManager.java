package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.shared.packets.addonpacket.ReceivedPublicChatMessageAddonPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AddonManager {
    public ServerSocket serverSocket;
    public List<AddonHandler> clients = new ArrayList<>();
    public Socket selfClient;
    public AddonHandler selfHandler;

    public AddonManager() throws IOException {
        if (BBsentials.generalConfig.isAlt()) {
            serverSocket = new ServerSocket(64987);
            BBsentials.executionService.execute(() -> {
                while (true) {
                    try {
                        Socket client = serverSocket.accept();
                        if (client.getInetAddress().isLoopbackAddress()) {
                            AddonHandler handler = new AddonHandler(client);
                            clients.add(handler);
                            BBsentials.executionService.execute(handler);
                        }
                        else client.close();
                    } catch (IOException e) {

                    }
                }
            });
        }
        else {
            try {
                    selfClient = new Socket("localhost", 64987);
                selfHandler = new AddonHandler(selfClient);
                Chat.sendPrivateMessageToSelfSuccess("Connected to the Addon Socket");
                clients.add(selfHandler);
                BBsentials.executionService.execute(selfHandler);
            } catch (Exception e) {
                Chat.sendPrivateMessageToSelfSuccess("Error trying to connect to the Addon Socket");
                BBsentials.executionService.schedule(() -> {
                    try {
                        BBsentials.addonManager = new AddonManager();
                    } catch (IOException ignored) {

                    }
                }, 20, TimeUnit.SECONDS);
            }
        }
    }

    public <T extends AbstractAddonPacket> void broadcastToAllAddons(T packet) {
        if (BBsentials.generalConfig.isAlt()) clients.forEach((client) -> client.sendPacket(packet));
        else {
            if (selfHandler != null && selfHandler.client.isConnected()) selfHandler.sendPacket(packet);
        }
    }

    public void notifyAllAddonsReceievedMessage(Message message) {
        broadcastToAllAddons(new ReceivedPublicChatMessageAddonPacket(message));
    }

    public boolean isConnected() {
        if (BBsentials.generalConfig.isAlt()) {
            return !clients.isEmpty();
        }
        else {
            return selfHandler != null && selfHandler.client.isConnected() && !selfHandler.client.isClosed();
        }
    }
}
