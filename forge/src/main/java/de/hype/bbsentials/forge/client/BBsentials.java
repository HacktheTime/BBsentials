package de.hype.bbsentials.forge.client;

import de.hype.bbsentials.forge.chat.Chat;
import de.hype.bbsentials.forge.client.Commands.CommandsOLD;
import de.hype.bbsentials.forge.communication.BBsentialConnection;


public class BBsentials {
    private boolean initialised = false;
    public static Config config;
    public static BBsentialConnection bbserver;
    public static CommandsOLD coms;

    public void init() {
        if (!initialised) {
            config = Config.load();
            Chat chat = new Chat();
            if (Config.isBingoTime() || config.overrideBingoTime()) {
                connectToBBserver();
            }
            initialised = true;
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        if (bbserver != null) {
            bbserver.sendHiddenMessage("exit");
        }
        bbserver = new BBsentialConnection();
        bbserver.setMessageReceivedCallback(message -> bbserver.onMessageReceived(message));
        bbserver.connect(config.getBBServerURL(), 5000);
    }

    public static void refreshCommands() {
        coms = new CommandsOLD();
    }

}
