package de.hype.bbsentials.fabric;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.updatelisteners.ChChestUpdateListener;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.RouteNode;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.fabric.objects.WorldRenderLastEvent;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.EnumUtils;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.ChChestData;
import kotlin.Unit;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Utils implements de.hype.bbsentials.client.common.mclibraries.Utils {
    public static boolean isBingo(PlayerEntity player) {
        try {
            return player.getDisplayName().getString().contains("Ⓑ");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIronman(PlayerEntity player) {
        try {
            return player.getDisplayName().getString().contains("♻");
        } catch (Exception e) {
            return false;
        }
    }

    public static void renderWaypoints(WorldRenderLastEvent event) {
        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        List<Waypoints> waypoints = Waypoints.waypoints.values().stream().filter((waypoint) -> waypoint.visible).toList();
        if (!waypoints.isEmpty()) {
            RenderInWorldContext.renderInWorld(event, (it) -> {
                for (Waypoints waypoint : waypoints) {
                    BlockPos pos = new BlockPos(waypoint.position.x, waypoint.position.y, waypoint.position.z);
                    if (playerPos.toCenterPos().distanceTo(pos.toCenterPos()) >= waypoint.renderDistance) continue;
                    it.color(0f, 1f, 0f, 0.2f);
                    it.block(pos);
                    it.color(1f, 0f, 0f, 1f);
                    it.waypoint(pos, Text.Serialization.fromJson(waypoint.jsonToRenderText));
                }
                return Unit.INSTANCE;
            });
        }
        try {
            if (BBsentials.temporaryConfig.route != null) {
                RenderInWorldContext.renderInWorld(event, (it) -> {
                    RouteNode node = BBsentials.temporaryConfig.route.getCurrentNode();
                    BlockPos pos = new BlockPos(node.coords.x, node.coords.y, node.coords.z);
                    BBsentials.temporaryConfig.route.doNextNodeCheck(playerPos.toCenterPos().distanceTo(pos.toCenterPos()));
                    it.color(node.color.getRed(), node.color.getGreen(), node.color.getBlue(), 0.2f);
                    it.block(pos);
                    it.color(node.color.getRed(), node.color.getGreen(), node.color.getBlue(), 1f);
                    it.waypoint(pos, Text.of(node.name));

                    return Unit.INSTANCE;
                });
            }
        } catch (Exception ignored) {
        }
//        WorldRenderLastEvent.Companion.publish(event);
    }

    public static void doBingoRankManipulations(ItemStack stack) {
//        try {
//            NbtCompound nbt = stack.getOrCreateNbt();
//            NbtCompound displayTag = nbt.getCompound("display");
//
//            if (displayTag.contains("Lore")) {
//
//                NbtList loreList = displayTag.getList("Lore", NbtList.STRING_TYPE);
//                for (int i = 0; i < loreList.size(); i++) {
//                    String lineJson = loreList.getString(i);
//                    String lineContentString = Text.Serialization.fromLenientJson(lineJson).getString();
//                    if (lineContentString.matches("  #(\\d+) contributor")) {
//                        loreList.remove(i);
//                    }
//                    if (lineContentString.matches("  Top \\d+(\\.\\d+)%$") || lineContentString.matches("  Top \\d+%$")) {
//                        loreList.set(i, NbtString.of(Text.Serialization.toJsonString(Text.of("  §8 Top §a0%"))));
//                        loreList.add(i + 1, NbtString.of(Text.Serialization.toJsonString(Text.of("  §6§l#1§r §fcontributor"))));
//                        i += 1;
//                        continue;
//                    }
//                    if (lineContentString.contains("Playtime: ")){
////                        loreList.set(i, NbtString.of(Text.Serialization.toJsonString(Text.of("§7Playtime: §a0m 10s"))));
//                        continue;
//                    }
//                    if (lineContentString.contains("Contribution: ")){
//                        loreList.set(i, NbtString.of(Text.Serialization.toJsonString(Text.of("§7Contribution: §a2,147,483,647 experience"))));
//                        continue;
//                    }
//
//                }
//                if (!loreList.get(loreList.size() - 1).asString().contains("This is faked!")) {
////                    loreList.add(NbtString.of(Text.Serialization.toJsonString(Text.of("§4This is faked!"))));
//                }
//
//                displayTag.put("Lore", loreList);
//                stack.getNbt().put("display", displayTag);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void addDebugInfoToRender(ItemStack stack) {
        try {
            if (stack.getNbt().getBoolean("addedDebug")) return;
            NbtCompound nbt = stack.getOrCreateNbt();
            NbtCompound displayTag = nbt.getCompound("display");
            NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
            NbtList loreList = displayTag.getList("Lore", NbtList.STRING_TYPE);
            Set<String> keys = extraAttributes.getKeys();
            for (String key : keys) {
                if (key.equals("enchantments")) continue;
                if (key.equals("timestamp")) {
                    Long stamp = extraAttributes.getLong(key);
                    loreList.add(NbtString.of(Text.Serialization.toJsonString(Text.of("timestamp(Creation Date): " + stamp + "(" + new Date(stamp) + ")"))));
                    continue;
                }
                loreList.add(NbtString.of(Text.Serialization.toJsonString(Text.of(key + ": " + extraAttributes.get(key)))));
            }
            displayTag.put("Lore", loreList);
            stack.getNbt().put("display", displayTag);
            stack.getNbt().putBoolean("addedDebug", true);
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isWindowFocused() {
        return MinecraftClient.getInstance().isWindowFocused();
    }

    public File getConfigPath() {
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        File bbsentialsDir = new File(configDir, "BBsentials");

        // Create the folder if it doesn't exist
        if (!bbsentialsDir.exists()) {
            boolean created = bbsentialsDir.mkdirs();
            if (!created) {
                // Handle the case where folder creation fails
                throw new RuntimeException("Failed to create Config folder");
            }
        }

        return bbsentialsDir;
    }

    public String getUsername() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    public String getMCUUID() {
        return MinecraftClient.getInstance().getSession().getUuidOrNull().toString();
    }

    public void playsound(String eventName) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(new Identifier(eventName)), 1.0F, 1.0F));
    }

    public int getPotTime() {
        int remainingDuration = 0;
        StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.STRENGTH);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingDuration = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingDuration;
    }

    public String mojangAuth(String serverId) {
        boolean success = false;
        int tries = 10;
        while (tries > 0 && !success) {
            tries--;
            try {
                MinecraftClient.getInstance().getSessionService().joinServer(MinecraftClient.getInstance().getGameProfile().getId(), MinecraftClient.getInstance().getSession().getAccessToken(), serverId);
                success = true;
            } catch (AuthenticationException e) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                if (tries == 0) {
                    Chat.sendPrivateMessageToSelfError("Could not authenticate at mojang: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return serverId;
    }

    public List<PlayerEntity> getAllPlayers() {
        List<PlayerEntity> players = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerEntity player : MinecraftClient.getInstance().player.getEntityWorld().getPlayers()) {
            if (!player.getDisplayName().getString().startsWith("!")) {
                if (Pattern.compile("\"color\":\"(?!white)\\w+\"").matcher(Text.Serialization.toJsonString(player.getDisplayName())).find()) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public boolean isInRadius(ClientPlayerEntity referencePlayer, PlayerEntity player, double radius) {
        return player != referencePlayer && player.squaredDistanceTo(referencePlayer) <= radius * radius;
    }

    public List<PlayerEntity> filterOut(List<PlayerEntity> players, Predicate<PlayerEntity> predicate) {
        return players.stream().filter(predicate).toList();
    }

    private List<PlayerEntity> getSplashLeechingPlayersPlayerEntity() {
        List<PlayerEntity> players = getAllPlayers();
        players.remove(MinecraftClient.getInstance().player);
        return filterOut(filterOut(getAllPlayers(), (player -> !isBingo(player))), (player) -> isInRadius(MinecraftClient.getInstance().player, player, 5));
    }

    public List<String> getSplashLeechingPlayers() {
        return getSplashLeechingPlayersPlayerEntity().stream().map((player -> player.getDisplayName().getString())).toList();
    }

    public InputStream makeScreenshot() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        AtomicReferenceArray<InputStream> screenshotInputStream = new AtomicReferenceArray<>(new InputStream[1]);
        AtomicBoolean isWaiting = new AtomicBoolean(true);

        // Execute the screenshot task on the main thread
        minecraftClient.execute(() -> {
            try {
                ByteBuffer buffer = ByteBuffer.wrap(ScreenshotRecorder.takeScreenshot(minecraftClient.getFramebuffer()).getBytes());

                byte[] byteArray = new byte[buffer.capacity()];
                buffer.get(byteArray);

                synchronized (screenshotInputStream) {
                    screenshotInputStream.set(0, new ByteArrayInputStream(byteArray));
                    isWaiting.set(false);
                    screenshotInputStream.notifyAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        synchronized (screenshotInputStream) {
            // Wait for the task to be completed or a timeout if needed
            while (isWaiting.get()) {
                try {
                    screenshotInputStream.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
        }

        return screenshotInputStream.get(0);
    }


    @Override
    public String getStringFromTextJson(String textJSon) throws Exception {
        try {
            return Text.Serialization.fromJson(textJSon).getString();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean executeClientCommand(String command) {
        return ClientCommandInternals.executeCommand(command);
    }

    @Override
    public boolean isJsonParseableToText(String json) {
        try {
            Text.Serialization.fromJson(json);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public String stringToTextJson(String string) {
        if (isJsonParseableToText(string)) return string;
        return Text.Serialization.toJsonString(Text.of(string));
    }

    @Override
    public List<String> getPlayers() {
        return getAllPlayers().stream().map((playerEntity) -> playerEntity.getDisplayName().getString()).toList();
    }

    public void renderOverlays(DrawContext drawContext, float v) {
        if (UpdateListenerManager.splashStatusUpdateListener.showOverlay()) {
            // Set the starting position for the overlay
            int x = 10;
            int y = 10;

            // Render each string in the list
            List<PlayerEntity> splashLeechers = getSplashLeechingPlayersPlayerEntity();
            List<PlayerEntity> allParticipiants = filterOut(getAllPlayers(), (player) -> isInRadius(MinecraftClient.getInstance().player, player, 5));
            List<PlayerEntity> musicPants = new ArrayList<>();
            List<Text> toDisplay = new ArrayList<>();
            toDisplay.add(Text.of("§6Total: " + allParticipiants.size() + " | Bingos: " + (allParticipiants.size() - splashLeechers.size()) + " | Leechers: " + splashLeechers.size()));
            boolean doPants = BBsentials.hudConfig.showMusicPants;
            for (PlayerEntity participiant : allParticipiants) {
                if (doPants) {

                    boolean hasPants = false;
                    for (ItemStack armorItem : participiant.getArmorItems()) {
                        try {
                            if (armorItem.getNbt().get("ExtraAttributes").asString().contains("MUSIC_PANTS")) {
                                musicPants.add(participiant);
                                hasPants = true;
                            }
                        } catch (Exception ignored) {
                            continue;
                        }
                    }
                    if (hasPants) {
                        String pantsAddition = Text.Serialization.toJsonString(Text.of("§4[♪]§ "));
                        String normal = Text.Serialization.toJsonString(participiant.getDisplayName());
                        toDisplay.add(Text.Serialization.fromJson("[" + pantsAddition + "," + normal + "]"));
                    }
                }
            }
            toDisplay.addAll(splashLeechers.stream().map(PlayerEntity::getDisplayName).toList());
            for (Text text : toDisplay) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
                y += 10; // Adjust the vertical position for the next string
            }
        }
        if (UpdateListenerManager.chChestUpdateListener.showOverlay()) {
            ChChestUpdateListener listener = UpdateListenerManager.chChestUpdateListener;

            int x = 10;
            int y = 15;
            List<Text> toRender = new ArrayList<>();
            if (listener.isHoster) {
                String status = listener.lobby.getStatus();
                switch (status) {
                    case "Open":
                        status = "§aOpen";
                        break;
                    case "Closed":
                        status = "§4Closed";
                        break;
                    case "Full":
                        status = "Full";
                        break;
                }
                String warpInfo = "§cFull";
                int playerThatCanBeWarped = EnvironmentCore.utils.getMaximumPlayerCount() - EnvironmentCore.utils.getPlayerCount();
                if (playerThatCanBeWarped >= 1) {
                    warpInfo = "§a(" + playerThatCanBeWarped + ")";
                }

                toRender.add(Text.of("§6Status:§0 " + status + "§6 | Slots: " + warpInfo + "§6"));
                long closingTimeInMinutes = ((360000 - EnvironmentCore.utils.getLobbyTime()) * 50) / 60000;
                if (closingTimeInMinutes <= 0) {
                    toRender.add(Text.of("§4Lobby Closed"));
                }
                else {
                    toRender.add(Text.of("§6Closing in " + closingTimeInMinutes / 60 + "h | " + closingTimeInMinutes % 60 + "m"));
                }
            }
            else {
                toRender.add(Text.of("§4Please Leave the Lobby after getting all the Chests to allow people to be warped in!"));
                for (ChChestData chest : listener.getUnopenedChests()) {
                    String author = "";
                    if (!listener.lobby.contactMan.equalsIgnoreCase(chest.finder)) author = " [" + chest.finder + "]";
                    toRender.add(Text.of("(" + chest.coords.toString() + ")" + author + ":"));
                    Arrays.stream(chest.items).map(ChChestItem::getDisplayName).forEach((string) -> toRender.add(Text.of(string)));
                }
            }
            for (Text text : toRender) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
                y += 10; // Adjust the vertical position for the next string
            }
        }
    }

    public Islands getCurrentIsland() {
        try {
            String string = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-b").getDisplayName().getString();
            if (!string.startsWith("Area: ")) {
                Chat.sendPrivateMessageToSelfError("Could not get Area data. Are you in Skyblock?");
            }
            else {
                return EnumUtils.getEnumByValue(Islands.class, string.replace("Area: ", "").trim());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public int getPlayerCount() {
        return Integer.parseInt(MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!B-a").getDisplayName().getString().trim().replaceAll("[^0-9]", ""));
    }

    public String getServerId() {
        PlayerListEntry entry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-c");
        if (entry == null) return null;
        return entry.getDisplayName().getString().replace("Server:", "").trim();
    }

    public boolean isOnMegaServer() {
        return getServerId().toLowerCase().startsWith("mega");
    }

    public boolean isOnMiniServer() {
        return getServerId().toLowerCase().startsWith("mini");
    }

    public int getMaximumPlayerCount() {
        boolean mega = isOnMegaServer();
        Islands island = getCurrentIsland();
        if (island == null) return 100;
        if (island.equals(Islands.HUB)) {
            if (mega) return 80;
            else return 24;
        }
        return 24;
    }

    @Override
    public void systemExit(int id) {
        System.exit(id);
    }

    public long getLobbyTime() {
        return MinecraftClient.getInstance().world.getLevelProperties().getTimeOfDay();
    }
}