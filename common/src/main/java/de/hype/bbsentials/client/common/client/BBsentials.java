package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Sender;
import de.hype.bbsentials.client.common.client.commands.Commands;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.client.socketAddons.AddonManager;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.config.*;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BBsentials {
    public static final Sender sender = new Sender();
    public static BBsentialConnection connection;
    public static Commands coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    public static Map<Integer, ServerSwitchTask> onServerJoin = new HashMap<>();
    public static Map<Integer, ServerSwitchTask> onServerLeave = new HashMap<>();

    public static Map<Integer, CustomItemTexture> customItemTextures = new HashMap<>();
    public static Thread bbthread;
    public static Chat chat = new Chat();
    public static Thread debugThread;
    //General Config needs to be first config!
    public static GeneralConfig generalConfig = new GeneralConfig();
    //All Other Configs
    public static DeveloperConfig developerConfig = new DeveloperConfig();
    public static DiscordConfig discordConfig = new DiscordConfig();
    public static SocketAddonConfig socketAddonConfig = new SocketAddonConfig();
    public static ChChestConfig chChestConfig = new ChChestConfig();
    public static FunConfig funConfig = new FunConfig();
    public static TemporaryConfig temporaryConfig = new TemporaryConfig();
    public static MiningEventConfig miningEventConfig = new MiningEventConfig();
    public static PartyConfig partyConfig = new PartyConfig();
    public static VisualConfig visualConfig = new VisualConfig();
    public static SplashConfig splashConfig = new SplashConfig();
    public static HUDConfig hudConfig = new HUDConfig();
    public static GuildConfig guildConfig = new GuildConfig();
    public static BBServerConfig bbServerConfig = new BBServerConfig();
    public static EnvironmentConfig environmentConfig = new EnvironmentConfig();
    public static AddonManager addonManager;
    private static boolean initialised = false;

    public static void connectToBBserver() {
        connectToBBserver(bbServerConfig.connectToBeta);
    }

    /**
     * Checks if still connected to the Server.
     *
     * @return true if it connected; false if old connection is kept.
     */
    public static boolean conditionalReconnectToBBserver() {
        if (!connection.isConnected()) {
            Chat.sendPrivateMessageToSelfInfo("Reconnecting");
            connectToBBserver(bbServerConfig.connectToBeta);
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
                    connection.connect(bbServerConfig.bbServerURL, 5011);
                }
                else {
                    connection.connect(bbServerConfig.bbServerURL, 5000);
                }
            });
            bbthread.start();
        });
    }

    /**
     * Runs the mod initializer on the client environment.
     */

    public synchronized static void onServerJoin() {
        onServerLeave();
        executionService.schedule(() -> {
            for (ServerSwitchTask task : onServerJoin.values()) {
                if (!task.permanent) {
                    onServerJoin.remove(task.getId());
                }
                task.run();
            }
        }, 5, TimeUnit.SECONDS);
    }

    public static void onServerLeave() {
        for (ServerSwitchTask task : onServerLeave.values()) {
            if (!task.permanent) {
                onServerLeave.remove(task.getId()).run();
            }
            task.run();
        }
    }

    public static void init() {
        debugThread = new Thread(
                EnvironmentCore.debug
        );
        debugThread.start();
        debugThread.setName("Debug Thread");
        if (GeneralConfig.isBingoTime() || bbServerConfig.overrideBingoTime) {
            connectToBBserver();
            UpdateListenerManager.init();
            EnvironmentCore.mcevents.registerAll();
        }
        try {
            addonManager = new AddonManager();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
            e.printStackTrace();
        }
    }
}