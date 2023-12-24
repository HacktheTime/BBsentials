package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.objects.ChestLobbyData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateListenerManager {
    public static BBsentialConnection connection;
    public static SplashStatusUpdateListener splashStatusUpdateListener;
    public static ChChestUpdateListener chChestUpdateListener;
    public static List<ChestLobbyData> lobbies;

    public static void init(BBsentialConnection connestion) {
        splashStatusUpdateListener = new SplashStatusUpdateListener(connestion, null);
        EnvironmentCore.utils.registerOverlays();
        lobbies = new ArrayList<>();
        chChestUpdateListener = new ChChestUpdateListener(connestion, null);
        EnvironmentCore.utils.registerOverlays();
        BBsentials.onServerJoin.add(new ServerSwitchTask(() -> permanentCheck()));
    }

    public static void permanentCheck() {
        BBsentials.executionService.execute(() -> {
            String serverId = EnvironmentCore.utils.getServerId();
            int index = lobbies.stream().map((lobby) -> lobby.serverId).collect(Collectors.toList()).indexOf(serverId);
            if (index != -1) {
                chChestUpdateListener = new ChChestUpdateListener(connection, lobbies.get(index));
                chChestUpdateListener.run();
            }
        });
    }

    public static void registerChest(ChestLobbyData data) {
        if (data.getStatus().equalsIgnoreCase("Closed")) {
            lobbies.remove(data);
            return;
        }
        int index = lobbies.indexOf(data);
        if (index != -1) lobbies.set(index, data);
        else lobbies.add(data);
        permanentCheck();
    }
}
