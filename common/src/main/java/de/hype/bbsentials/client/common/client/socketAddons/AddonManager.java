package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.shared.packets.addonpacket.ReceivedPublicChatMessageAddonPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AddonManager {
    public ServerSocket serverSocket;
    List<AddonHandler> clients = new ArrayList<>();

    public AddonManager() throws IOException {
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

    public <T extends AbstractAddonPacket> void broadcastToAllAddons(T packet) {
        clients.forEach((client) -> client.sendPacket(packet));
    }

    public void notifyAllAddonsReceievedMessage(Message message) {
        broadcastToAllAddons(new ReceivedPublicChatMessageAddonPacket(message));
    }
}
