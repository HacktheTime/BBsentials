package de.hype.bbsentials.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.common.client.DebugThread {
    boolean doTest = false;

    public static List<Object> store = new ArrayList<>();
    @Override
    public void loop() {
        if (doTest) {
            doTest = false;
            test();
        }
    }

    public void onNumpadCode() {

    }

    public void doOnce() {
        doTest = true;
    }

    @Override
    public List<String> test() {
        return List.of("");
    }

    public List<PlayerEntity> getAllPlayers() {
        List<PlayerEntity> players = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerEntity player : MinecraftClient.getInstance().player.getEntityWorld().getPlayers()) {
            if (!player.getDisplayName().getString().startsWith("!")) {
                players.add(player);
            }
        }

        return players;
    }

    public List<PlayerEntity> getPlayersInRadius(ClientPlayerEntity referencePlayer, List<PlayerEntity> players, double radius) {
        List<PlayerEntity> nearbyPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerEntity player : players) {
            if (player != referencePlayer && player.squaredDistanceTo(referencePlayer) <= radius * radius) {
                nearbyPlayers.add(player);
            }
        }

        return nearbyPlayers;
    }

    public List<PlayerEntity> getNonBingoPlayers(List<PlayerEntity> players) {
        List<PlayerEntity> nonBingoPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerEntity player : players) {
            if (player.getCustomName().getString().contains("Ⓑ")) {
                nonBingoPlayers.add(player);
            }
        }
        return nonBingoPlayers;
    }

    public List<String> getSplashLeechingPlayers() {
        List<PlayerEntity> players = getAllPlayers();
        players.remove(MinecraftClient.getInstance().player);
        return getPlayersInRadius(MinecraftClient.getInstance().player, getNonBingoPlayers(players), 5).stream().map((playerEntity -> playerEntity.getDisplayName().getString())).toList();
    }
}
