package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Sender;
import de.hype.bbsentials.client.common.client.commands.Commands;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.client.socketAddons.AddonManager;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.config.*;
import de.hype.bbsentials.client.common.discordintegration.DiscordIntegration;
import de.hype.bbsentials.client.common.discordintegration.GameSDKManager;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.WaypointRoute;
import de.hype.bbsentials.shared.constants.Islands;

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
    public static DiscordIntegration discordIntegration = new DiscordIntegration();
    public static AddonManager addonManager;
    public static GameSDKManager dcGameSDK;
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
        }
        try {
            addonManager = new AddonManager();
            UpdateListenerManager.init();
            EnvironmentCore.mcevents.registerAll();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
            e.printStackTrace();
        }
        WaypointRoute.waypointRouteDirectory.mkdirs();
        ServerSwitchTask.onServerJoinTask(() -> {
            Islands island = EnvironmentCore.utils.getCurrentIsland();
            String status = "Lobby Gaming";
            if (island != null) status = "Playing in the " + island.getDisplayName();
            BBsentials.discordIntegration.setNewStatus(status);
        }, true);
        if (discordConfig.useRichPresence) {
            try {
                dcGameSDK = new GameSDKManager();
                if (discordConfig.useRichPresence) {
                    dcGameSDK.updateActivity();
                    ServerSwitchTask.onServerJoinTask(() -> dcGameSDK.updateActivity(), true);
                }
            } catch (Exception e) {
                Chat.sendPrivateMessageToSelfError("Could not set Discord Rich Presence");
            }
        }
    }
}