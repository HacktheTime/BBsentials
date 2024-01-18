package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.client.BBsentials;

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
        BBsentials.executionService.execute(()->{
            while (true){
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

    public void broadcastToAllAddons(String message){
        clients.forEach((client)->client.sendMessage(message));
    }
}
