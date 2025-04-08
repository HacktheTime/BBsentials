package de.hype.bingonet.client.common.client.socketAddons;

import de.hype.bingonet.client.common.chat.Message;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.shared.packets.addonpacket.ReceivedPublicChatMessageAddonPacket;

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
        BingoNet.executionService.execute(() -> {
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    if (client.getInetAddress().isLoopbackAddress() && BingoNet.socketAddonConfig.useSocketAddons) {
                        AddonHandler handler = new AddonHandler(client);
                        clients.add(handler);
                        BingoNet.executionService.execute(handler);
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
