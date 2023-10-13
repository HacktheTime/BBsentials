package de.hype.bbsentials.common.client;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.Commands.CommandsOLD;
import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.mclibraries.Options;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BBsentials {
    public static Config config;
    public static BBsentialConnection connection;
    public static CommandsOLD coms;
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
        }
        if (bbthread != null) {
            if (bbthread.isAlive()) {
                bbthread.interrupt();
            }
        }
        bbthread = new Thread(() -> {
            connection = new BBsentialConnection();
            coms = new CommandsOLD();
            connection.setMessageReceivedCallback(message -> executionService.execute(() -> connection.onMessageReceived(message)));
            if (beta) {
                connection.connect(config.getBBServerURL(), 5011);
            }
            else {
                connection.connect(config.getBBServerURL(), 5000);
            }
            executionService.scheduleAtFixedRate(new DebugThread(), 0, 20, TimeUnit.SECONDS);
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
                if (config.doGammaOverride) Options.setGamma(10);
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