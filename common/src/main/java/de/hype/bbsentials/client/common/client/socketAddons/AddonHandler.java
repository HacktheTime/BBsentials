package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.ChatPrompt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AddonHandler implements Runnable {
    public Socket client;
    private BufferedReader reader;
    private PrintWriter writer;

    public AddonHandler(Socket client) {
        this.client=client;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception ignored) {
            try {
                close();
            } catch (Exception ignoredtoo) {

            }
        }
    }

    public void sendMessage(String message) {
        writer.write(message);
    }

    public void onReceive(String message) {
        message = message.replace("/n","\n");
        if (message == null) {
            close();
            return;
        }

//        Chat.sendPrivateMessageToSelfDebug(message);
        // Check for command prefixes
        if (message.startsWith("publicchat ")) {
            if (!BBsentials.socketAddonConfig.allowAutomatedSending) return;
            handlePublicChat(message.substring("publicchat ".length()));
        } else if (message.startsWith("clientsidechat ")) {
            handleClientSideChat(message.substring("clientsidechat ".length()));
        } else if (message.startsWith("rfunctioncommand ")) {
            if (!BBsentials.socketAddonConfig.allowChatPrompt) return;
            rfunctioncommand(message.substring("rfunctioncommand ".length()));
        } else if (message.startsWith("clientsidetellraw ")) {
            if (!BBsentials.socketAddonConfig.allowTellraw) return;
            handleClientSideTellraw(message.substring("clientsidetellraw ".length()));
        } else if (message.startsWith("playsound ")) {
            handlePlaySound(message.substring("playsound ".length()));
        } else if (message.startsWith("clientcommand ")) {
            if (!BBsentials.socketAddonConfig.allowClientCommands) return;
            handleClientCommand(message.substring("clientcommand ".length()));
        } else if (message.startsWith("servercommand ")) {
            if (!BBsentials.socketAddonConfig.allowAutomatedSending) return;
            handleServerCommand(message.substring("servercommand ".length()));
        }
    }

    private void rfunctioncommand(String message) {
        BBsentials.temporaryConfig.lastChatPromptAnswer=new ChatPrompt(message,10);
    }

    private void handlePublicChat(String message) {
        BBsentials.sender.addSendTask(message);
    }

    private void handleClientSideChat(String message) {
        Chat.sendPrivateMessageToSelfInfo(message);
    }

    private void handleClientSideTellraw(String message) {
        Chat.sendPrivateMessageToSelfText(Message.tellraw(message));
    }

    /**
     * @param message like block.anvil.destroy
     */
    private void handlePlaySound(String message) {
        EnvironmentCore.utils.playsound(message);
    }

    private void handleClientCommand(String message) {
        EnvironmentCore.utils.executeClientCommand(message);
    }

    private void handleServerCommand(String message) {
       sendMessage("/"+message);
    }


    @Override
    public void run() {
        while (client.isConnected()){
            try {
                onReceive(reader.readLine());
            } catch (Exception ignored) {

            }
        }
        BBsentials.addonManager.clients.remove(this);
    }

    public void close(){
        try {
            client.close();
        } catch (IOException e) {

        }
        reader=null;
        writer=null;
    }
}
