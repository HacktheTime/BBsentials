package de.hype.bbsentials.common.client;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.Commands.Commands;
import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BBsentials {
    public static Config config;
    public static BBsentialConnection connection;
    public static Commands coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    public static boolean splashLobby;
    private static Thread bbthread;
    private static boolean initialised = false;
    public static SplashStatusUpdateListener splashStatusUpdateListener;

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        connectToBBserver(config.connectToBeta);
    }

    /**
     * Checks if still connected to the Server.
     *
     * @return true if it connected; false if old connection is kept.
     */
    public static boolean conditionalReconnectToBBserver() {
        if (!connection.isConnected()) {
            Chat.sendPrivateMessageToSelfInfo("Reconnecting");
            connectToBBserver(config.connectToBeta);
            return true;
        }
        return false;
    }

    public static void connectToBBserver(boolean beta) {
        if (connection != null) {
            connection.sendHiddenMessage("exit");
            connection.close();
        }
        connection=null;
        if (bbthread != null) {
            if (bbthread.isAlive()) {
                bbthread.interrupt();
            }
        }
        bbthread = new Thread(() -> {
            connection = new BBsentialConnection();
            coms = new Commands();
            connection.setMessageReceivedCallback(message -> executionService.execute(() -> connection.onMessageReceived(message)));
            if (beta) {
                connection.connect(config.getBBServerURL(), 5011);
            }
            else {
                connection.connect(config.getBBServerURL(), 5000);
            }
            executionService.scheduleAtFixedRate(EnvironmentCore.debug, 0, 20, TimeUnit.SECONDS);
        });
        bbthread.start();
    }

    /**
     * Runs the mod initializer on the client environment.
     */

    public static void onServerSwap() {
            splashLobby = false;
            if (!initialised) {
                config = Config.load();
                if (config.doGammaOverride) EnvironmentCore.mcoptions.setGamma(10);
                Chat chat = new Chat();
                if (Config.isBingoTime() || config.overrideBingoTime()) {
                    connectToBBserver();
                }
                initialised = true;
            }
    }
    public void manualLoad() {
        initialised = true;
        config = Config.load();
        connectToBBserver();
    }
}