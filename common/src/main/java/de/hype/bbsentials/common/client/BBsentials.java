package de.hype.bbsentials.common.client;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.Commands.Commands;
import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BBsentials {
    public static Config config;
    public static BBsentialConnection connection;
    private static boolean initialised = false;
    public static Commands coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    public static boolean splashLobby;
    public static SplashStatusUpdateListener splashStatusUpdateListener;
    public static Thread bbthread;
    public static Chat chat = new Chat();
    public static Thread debugThread;

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
        executionService.execute(() -> {
            if (connection != null) {
                connection.close();
            }
            bbthread = new Thread(() -> {
                connection = new BBsentialConnection();
                coms = new Commands();
                if (beta) {
                    connection.connect(config.getBBServerURL(), 5011);
                }
                else {
                    connection.connect(config.getBBServerURL(), 5000);
                }
            });
            bbthread.start();
        });
    }

    /**
     * Runs the mod initializer on the client environment.
     */

    public static void onServerSwap() {
        if (!initialised) {
            initialised = true;
            if (Config.isBingoTime() || config.overrideBingoTime()) {
                connectToBBserver();
            }
        }
        splashLobby = false;
    }

    public static void init() {
        config = Config.load();
        debugThread = new Thread(
                EnvironmentCore.debug
        );
        debugThread.start();
        debugThread.setName("Debug Thread");
    }
}