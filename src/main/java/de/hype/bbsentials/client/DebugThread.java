package de.hype.bbsentials.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public class DebugThread implements Runnable {

    @Override
    public void run() {
         loop();
        //place a breakpoint for only this thread here.
    }

    public void loop() {

    }

    public static List<String> test() {
        List<PlayerListEntry> tabList = MinecraftClient.getInstance().player.networkHandler.getPlayerList().stream().toList();
        List<PlayerListEntry> goodTabList = MinecraftClient.getInstance().player.networkHandler.getPlayerList().stream().toList();
        for (PlayerListEntry playerListEntry : tabList) {
            try {
                if (!playerListEntry.getProfile().getName().startsWith("!")) {
                    goodTabList.add(playerListEntry);
                }
            } catch (Exception ignored) {

            }
        }
        List<String> stringList = new ArrayList<>();
        for (PlayerListEntry playerListEntry : goodTabList) {
            try {
                String string = playerListEntry.getDisplayName().getString();
                String string2 = (string.replaceAll("\\[\\d+\\]", ""));
                if (!string.isEmpty()) {
                    if (!string.equals(string2)) {
                        stringList.add(string2);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return stringList;
    }

    public static List<String> playersOnTabList() {
        return test().stream().map((string) -> string.replaceAll("[^\\p{L}\\p{N}]+", "")).toList();
    }
}
