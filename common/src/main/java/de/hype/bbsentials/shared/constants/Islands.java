package de.hype.bbsentials.shared.constants;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * List of all Islands
 * {@link #CRYSTAL_HOLLOWS}
 * {@link #CRIMSON_ISLE}
 * {@link #DEEP_CAVERNS}
 * {@link #DUNGEON}
 * {@link #DUNGEON_HUB}
 * {@link #DWARVEN_MINES}
 * {@link #GOLD_MINE}
 * {@link #HUB}
 * {@link #KUUDRA}
 * {@link #PRIVATE_ISLAND}
 * {@link #SPIDERS_DEN}
 * {@link #THE_END}
 * {@link #THE_FARMING_ISLANDS}
 * {@link #JERRYS_WORKSHOP}
 * {@link #THE_RIFT}
 */
public enum Islands implements BBDisplayNameProvider {
    CRYSTAL_HOLLOWS("crystal_hollows", "Crystal Hollows", Islands::sendFallbackIsland, "/warp crystals"),
    CRIMSON_ISLE("crimson_isle", "Crimson Isle", Islands::sendFallbackIsland, "/warp isle"),
    DEEP_CAVERNS("mining_2", "Deep Caverns", Islands::sendFallbackIsland, "/warp deep"),
    DUNGEON("dungeon", "Dungeon", Islands::sendFallbackIsland, null, true, 35),
    DUNGEON_HUB("dungeon_hub", "Dungeon Hub", Islands::sendFallbackIsland, "/warp dhub"),
    DWARVEN_MINES("mining_3", "Dwarven Mines", () -> {
        if (BBsentials.pauseWarping) {
            sendFallbackIsland();
            BBsentials.sender.addSendTask("/l", 5);
        }
        else {
            sendFallbackIsland();
        }
    }, "/warp forge"),
    GOLD_MINE("mining_1", "Gold Mine", Islands::sendFallbackIsland, "/warp gold"),
    HUB("hub", "Hub", Islands::sendFallbackIsland, "/warp hub"),
    GLACITE_TUNNEL("mineshaft", "Mineshaft", Islands::sendFallbackIsland, "/warp forge"),
    KUUDRA("kuudra", "Kuudra", Islands::sendFallbackIsland, "/warp isle"),
    SPIDERS_DEN("combat_1", "Spider's Den", Islands::sendFallbackIsland, "/warp spider"),
    THE_END("combat_3", "The End", Islands::sendFallbackIsland, "/warp end"),
    THE_FARMING_ISLANDS("farming_1", "The Farming Islands", Islands::sendFallbackIsland, "/warp barn"),
    JERRYS_WORKSHOP("winter", "Jerry's Workshop", Islands::sendFallbackIsland, "/warp jerry"),
    THE_RIFT("rift", "The Rift", Islands::sendFallbackIsland, null),
    THE_PARK("foraging_1", "The Park", Islands::sendFallbackIsland, "/warp park"),
    Dark_Auction("dark_auction", "Dark Auction", Islands::sendFallbackIsland, null),
    GARDEN("garden", "Garden", () -> {
        BBsentials.sender.addImmediateSendTask("/setspawn");
        if (getPrivateIsland().isTravelSafe())
            BBsentials.sender.addSendTask("/is", 1); //Method used to avoid the Error of it bein undefined
        else BBsentials.sender.addSendTask("/l", 1);
    }, null, false, 30),
    PRIVATE_ISLAND("dynamic", "Private Island", () -> {
        BBsentials.sender.addImmediateSendTask("/setspawn");
        if (GARDEN.isTravelSafe()) BBsentials.sender.addSendTask("/warp garden", 1);
        else BBsentials.sender.addSendTask("/l", 1);
    }, null, false, 30);

    private static Map<String, Instant> playTimeUpdates = new HashMap<>();
    private final String internalName;
    private final String displayName;
    private final Runnable exitRunnable;
    private final String altTravelWarpCommand;
    private final boolean warpable;
    private final int delayPTCheckTime;
    private Instant lastLeaveTime = null;

    Islands(String internalName, String displayName, Runnable exitRunnable, String altWarpCommand, boolean warpable, Integer delayPTCheckTime) {
        this.internalName = internalName;
        this.displayName = displayName;
        this.exitRunnable = exitRunnable;
        this.altTravelWarpCommand = altWarpCommand;
        this.warpable = warpable;
        this.delayPTCheckTime = delayPTCheckTime;
    }

    Islands(String internalName, String displayName, Runnable exitRunnable, String altWarpCommand) {
        this(internalName, displayName, exitRunnable, altWarpCommand, true, 0);
    }

    public static void sendFallbackIsland() {
        String warpCommand;
        Islands fallBackIsland = getFallbackIsland();
        if (!fallBackIsland.isTravelSafe()) {
            if (fallBackIsland == PRIVATE_ISLAND) {
                if (GARDEN.isTravelSafe()) {
                    Chat.sendPrivateMessageToSelfError("Private Island was not unloaded long enough. Sending to Garden Instead.");
                    fallBackIsland = GARDEN;
                }
                else {
                    sendFallbackIsland("/l");
                }
            }
            else if (fallBackIsland == GARDEN) {
                if (GARDEN.isTravelSafe()) {
                    Chat.sendPrivateMessageToSelfError("Garden was not unloaded long enough. Sending to Private Island Instead.");
                    fallBackIsland = PRIVATE_ISLAND;
                }
                else {
                    sendFallbackIsland("/l");
                }
            }
            else {
                Chat.sendPrivateMessageToSelfError("Unknown Fallback Island");
                sendFallbackIsland("/l");
                return;
            }
        }
        if (fallBackIsland == PRIVATE_ISLAND) warpCommand = "/is";
        else if (fallBackIsland == GARDEN) warpCommand = "/warp garden";
        else warpCommand = fallBackIsland.getWarpCommand();
        if (warpCommand == null) {
            Chat.sendPrivateMessageToSelfError("Unknown Fallback Island");
            warpCommand = "/l";
        }
        sendFallbackIsland(warpCommand);
    }

    public static Islands getFallbackIsland() {
        return GARDEN;
    }

    public static void sendFallbackIsland(String warp) {
        BBsentials.sender.addImmediateSendTask(warp);
    }

    public static Islands getFromTravel(TravelEnums value) {
        for (Islands islands : values()) {
            if (islands == value.getIsland()) {
                return islands;
            }
        }
        return null;
    }

    private static Islands getPrivateIsland() {
        return PRIVATE_ISLAND;
    }

    public static void putPlaytimeUpdate(String serverId, Instant lastPlaytimeUpdate) {
        Chat.sendPrivateMessageToSelfInfo("PTU: sid %s | %ss ago".formatted(serverId, Duration.between(lastPlaytimeUpdate, Instant.now()).getSeconds()));
        playTimeUpdates.put(serverId, lastPlaytimeUpdate);
    }

    public static Instant getPlaytimeUpdate(String serverId) {
//        Chat.sendPrivateMessageToSelfInfo("Got Lobby PTU: %s".formatted(serverId));
        return playTimeUpdates.get(serverId);
    }

    public String getWarpCommand() {
        return altTravelWarpCommand;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Runnable getExitRunnable() {
        return exitRunnable;
    }

    public boolean canBeWarpedIn() {
        return warpable;
    }

    public int getDelayPTCheckTime() {
        return delayPTCheckTime;
    }

    public boolean isTravelSafe() {
        if (!isPersonalIsland()) return true;
        if (lastLeaveTime == null) return true;
        else if (lastLeaveTime.plusSeconds(20).isBefore(Instant.now()) && BBsentials.altDataStorage.getIsland() != this)
            return true;
        return false;
    }

    public boolean isPersonalIsland() {
        return this == PRIVATE_ISLAND || this == GARDEN;
    }

    public void setLastLeave(Instant lastLeave) {
        Chat.sendPrivateMessageToSelfInfo("Set Last Leave for %s".formatted(getDisplayName()));
        lastLeaveTime = lastLeave;
    }
}
