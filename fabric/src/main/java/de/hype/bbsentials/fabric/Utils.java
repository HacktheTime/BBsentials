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
import de.hype.bbsentials.shared.objects.Position;
import kotlin.Unit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.fabricmc.loader.api.FabricLoader;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.HypixelPacket;
import net.hypixel.modapi.serializer.PacketSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static de.hype.bbsentials.client.common.client.BBsentials.developerConfig;

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
            try {
                RenderInWorldContext.renderInWorld(event, (it) -> {
                    for (Waypoints waypoint : waypoints) {
                        BlockPos pos = new BlockPos(waypoint.position.x, waypoint.position.y, waypoint.position.z);
                        if (playerPos.toCenterPos().distanceTo(pos.toCenterPos()) >= waypoint.renderDistance) continue;
                        it.color(waypoint.color.getRed(), waypoint.color.getGreen(), waypoint.color.getBlue(), 0.2f);
                        it.block(pos);
                        it.color(waypoint.color.getRed(), waypoint.color.getGreen(), waypoint.color.getBlue(), 1f);
                        it.waypoint(pos, Text.Serialization.fromJson(waypoint.jsonToRenderText));
                        if (waypoint.doTracer) {
                            Vector3f cameraForward = new Vector3f(0f, 0f, 1f).rotate(event.camera.getRotation());
                            it.line(new Vec3d[]{event.camera.getPos().add(new Vec3d(cameraForward)), pos.toCenterPos()}, 3f);
                        }
                        it.doWaypointIcon(pos.toCenterPos(), waypoint.render, 25, 25);

                    }
                    return Unit.INSTANCE;
                });
            } catch (Exception e) {

            }
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
                    loreList.add(NbtString.of(Text.Serialization.toJsonString(Text.of("timestamp(Creation Date): " + stamp + "(" + Instant.ofEpochMilli(stamp) + ")"))));
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

    public UUID getMCUUIDID() {
        return MinecraftClient.getInstance().getSession().getUuidOrNull();
    }

    public void playsound(String eventName) {
        if (eventName.isEmpty()) MinecraftClient.getInstance().getSoundManager().stopAll();
        else
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

    public List<String> toDisplayStringLeecherOverlay() {
        List<String> stringList = new ArrayList();
        boolean doPants = BBsentials.splashConfig.showMusicPantsUsers;
        for (PlayerEntity player : getAllPlayers()) {
            String prefix = "";
            boolean display = false;
            if (!isBingo(player)) {
                display = true;
            }
            if (!isInRadius(MinecraftClient.getInstance().player, player, 5)) continue;
            if (doPants) {
                for (ItemStack armorItem : player.getArmorItems()) {
                    try {
                        if (armorItem.getNbt().get("ExtraAttributes").asString().contains("MUSIC_PANTS")) {
                            prefix = "§4[♪]§r ";
                            display = true;
                        }
                    } catch (Exception ignored) {
                        continue;
                    }
                }
            }

            Integer leechPotions = 0;
            for (Map.Entry<StatusEffect, StatusEffectInstance> entry : player.getActiveStatusEffects().entrySet()) {
                StatusEffect effect = entry.getKey();
                Integer amplifier = entry.getValue().getAmplifier();
                if (effect == StatusEffects.STRENGTH && amplifier >= 7) {
                    if (entry.getValue().getDuration() >= 60000) {
                        leechPotions++;
                    }
                }
                else if (effect == StatusEffects.JUMP_BOOST && amplifier >= 5) {
                    if (entry.getValue().getDuration() >= 60000) {
                        leechPotions++;
                    }
                }
            }
            if (leechPotions >= 2) {
                prefix += "§4[⏳] §r";
                display = true;
            }
            //32min left
            //Potion: Night VisionAmplifier: 0 Duration: 2147473747
            //Potion: StrengthAmplifier: 7 Duration: 39353
            //Potion: Jump BoostAmplifier: 5 Duration: 39520
            //Potion: HasteAmplifier: 3 Duration: 39379
            //Potion: AbsorptionAmplifier: 0 Duration: 39368
            if (display) {
                if (prefix.isEmpty()) stringList.add(Text.Serialization.toJsonString(player.getDisplayName()));
                else {
                    String prefixAddition = Text.Serialization.toJsonString(Text.of(prefix));
                    String normal = Text.Serialization.toJsonString(player.getDisplayName());
                    stringList.add("[" + prefixAddition + "," + normal + "]");
                }
            }
        }
        return stringList;
    }

    public boolean isSelfBingo() {
        assert MinecraftClient.getInstance().player != null;
        return Objects.requireNonNull(MinecraftClient.getInstance().player.getDisplayName()).getString().contains("Ⓑ");
    }

    public void displayToast(BBToast toast) {
        MinecraftClient.getInstance().getToastManager().add(toast);
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
    public Position getPlayersPosition() {
        BlockPos pos = MinecraftClient.getInstance().player.getBlockPos();
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public List<String> getPlayers() {
        return new ArrayList<>(MinecraftClient.getInstance().getNetworkHandler().getCommandSource().getPlayerNames().stream().toList());
    }

    public void renderOverlays(DrawContext drawContext, float v) {
        if (UpdateListenerManager.splashStatusUpdateListener.showOverlay()) {
            // Set the starting position for the overlay
            int x = 10;
            int y = 10;

            // Render each string in the list
            List<String> splashLeechers = toDisplayStringLeecherOverlay();
            List<PlayerEntity> allParticipiants = filterOut(getAllPlayers(), (player) -> isInRadius(MinecraftClient.getInstance().player, player, 5));
            List<Text> toDisplay = new ArrayList<>();
            toDisplay.add(Text.of("§6Total: " + allParticipiants.size() + " | Bingos: " + (allParticipiants.size() - splashLeechers.size()) + " | Leechers: " + splashLeechers.size()));

            toDisplay.addAll(splashLeechers.stream().map(Text.Serialization::fromJson).toList());
            for (Text text : toDisplay) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
                y += 10; // Adjust the vertical position for the next string
            }
        }
        else if (UpdateListenerManager.chChestUpdateListener.showOverlay()) {
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
                long closingTimeInMinutes = ((408000 - EnvironmentCore.utils.getLobbyTime()) * 50) / 60000;
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
                    chest.items.stream().map(ChChestItem::getDisplayName).forEach((string) -> toRender.add(Text.of(string)));
                }
            }
            for (Text text : toRender) {
                drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
                y += 10; // Adjust the vertical position for the next string
            }
        }
        else if (BBsentials.funConfig.lowPlayTimeHelpers && BBsentials.funConfig.lowPlaytimeHelperJoinDate != null) {
            long differece = ((Instant.now().getEpochSecond() - BBsentials.funConfig.lowPlaytimeHelperJoinDate.getEpochSecond()));
            String colorCode = "§a";
            if (differece > 50) colorCode = "§4§l";
            else if (differece > 45) colorCode = "§4";
            else if (differece > 40) colorCode = "§6";
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, Text.of(colorCode + "Time in Lobby: " + differece), 10, 10, 0xFFFFFF, true);
        }
    }

    public Islands getCurrentIsland() {
        try {
            ClientPlayNetworkHandler t = MinecraftClient.getInstance().getNetworkHandler();
            if (t == null) return null;
            String string;
            if (isSecondRowInfoRow()) {
                //Its in Second Row
                string = t.getPlayerListEntry("!B-b").getDisplayName().getString();
            }
            else {
                //Its 3 row. default from before
                string = t.getPlayerListEntry("!C-b").getDisplayName().getString();
            }
            if (!string.startsWith("Area: ") && !string.startsWith("Dungeon: ")) {
                Chat.sendPrivateMessageToSelfError("Could not get Area data. Are you in Skyblock?");
            }
            else {
                if (string.startsWith("Dungeon: ")) return Islands.DUNGEON;
                return EnumUtils.getEnumByValue(Islands.class, string.replace("Area: ", "").trim());
            }
        } catch (Exception e) {
        }
        return null;
    }

    private boolean isSecondRowInfoRow() {
        ClientPlayNetworkHandler t = MinecraftClient.getInstance().getNetworkHandler();
        if (t == null) return false;
        PlayerListEntry entry = t.getPlayerListEntry("!B-a");
        if (entry == null) return false;
        return entry.getDisplayName().getString().trim().startsWith("Info");
    }

    public int getPlayerCount() {
        ClientPlayNetworkHandler t = MinecraftClient.getInstance().getNetworkHandler();
        if (t == null) return 0;
        return t.getCommandSource().getPlayerNames().size();
    }

    public String getServerId() {
        ClientPlayNetworkHandler t = MinecraftClient.getInstance().getNetworkHandler();
        if (t == null) return "";
        PlayerListEntry entry;
        if (isSecondRowInfoRow()) {
            entry = t.getPlayerListEntry("!B-c");
        }
        else {
            entry = t.getPlayerListEntry("!C-c");
        }
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

    @Override
    public boolean isInGame() {
        return MinecraftClient.getInstance().player != null;
    }

    @Override
    public void showErrorScreen(String message) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        MinecraftClient.getInstance().execute((() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(screen), Text.of("§cBBsentials"), Text.of(message)))));
    }

    public long getLobbyTime() {
        return MinecraftClient.getInstance().world.getLevelProperties().getTimeOfDay();
    }

    public static class BBToast implements Toast {
        public static final int DEFAULT_DURATION_MS = 5000;
        private static final Identifier TEXTURE = new Identifier("toast/advancement");
        //        private static final Identifier TEXTURE = new Identifier("toast/system");
        String title;
        String description;
        Integer displayTime = DEFAULT_DURATION_MS;
        Identifier sound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE.getId();
        Identifier icon;
        Integer width;
        Integer height;
        Integer imageSize = 16;
        Integer integerToWrap = getWidth() - imageSize * 3;
        private boolean soundPlayed;

        public BBToast(String title, String description, Identifier sound, Identifier icon) {
            this.title = title;
            this.description = description;
            if (sound != null) this.sound = sound;
            if (icon != null) this.icon = icon;
        }

        public void setHeight() {
            height = MinecraftClient.getInstance().textRenderer.wrapLines(Text.of(description), integerToWrap).size() * (MinecraftClient.getInstance().textRenderer.fontHeight + 2) + 40;
        }

        @Override
        public int getWidth() {
            return Toast.super.getWidth() * 2;
        }

        @Override
        public int getHeight() {
            if (height == null) setHeight();
            return height;
        }

        public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
            int boxWidth = getWidth();
            int boxHeight = getHeight();
            int imageSize = 16;
            context.drawGuiTexture(TEXTURE, 0, 0, boxWidth, boxHeight);

            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(Text.of(description), integerToWrap);
            int textColor = 0xFFFFFF;

            if (list.size() == 1) {
                int titleY = (boxHeight - 18) / 2; // Center vertically
                context.drawText(manager.getClient().textRenderer, Text.of(title), imageSize * 2, titleY, textColor | -16777216, false);
                context.drawText(manager.getClient().textRenderer, list.get(0), imageSize * 2, titleY + 11, -1, false);
            }
            else {
                int titleColor = 0xFFFFFF;
                int fadeInColor = 67108864;
                int fadeOutColor = 67108864;

                if (startTime < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float) (1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | fadeInColor;
                    int titleY = (boxHeight - 18) / 2; // Center vertically
                    context.drawText(manager.getClient().textRenderer, Text.of(title), imageSize * 2, titleY, textColor | k, false);
                }
                else {
                    int k = MathHelper.floor(MathHelper.clamp((float) (startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | fadeOutColor;
                    int centerY = (boxHeight - list.size() * 9) / 2;

                    for (OrderedText orderedText : list) {
                        context.drawText(manager.getClient().textRenderer, orderedText, imageSize * 2, centerY, 16777215 | k, false);
                        centerY += 9;
                    }
                }
            }

            if (!this.soundPlayed && startTime > 0L) {
                this.soundPlayed = true;
                manager.getClient().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(sound), 1.0F, 1.0F));
            }

            context.drawItemWithoutEntity(Items.DIAMOND.getDefaultStack(), 1, 8, 8);
            return (double) startTime >= displayTime * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
        }

        public enum ToastType {
            ADVANCEMENT(new Identifier("toast/advancement")),
            SYSTEM(new Identifier("toast/system")),
            TUTORIAL(new Identifier("toast/tutorial")),
            RECIPE(new Identifier("toast/recipe")),
            ;
            private final Identifier id;

            ToastType(Identifier id) {
                this.id = id;
            }
        }
    }

    @Override
    public String getServerConnectedAddress() {
        try {
            return MinecraftClient.getInstance().getNetworkHandler().getConnection().getAddress().toString().split("/")[0];
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void registerNetworkHandlers() {
        for (String identifier : HypixelModAPI.getInstance().getRegistry().getIdentifiers()) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier(identifier), (client, handler, buf, responseSender) -> {
                buf.retain();
                client.execute(() -> {
                    try {
                        HypixelModAPI.getInstance().handle(identifier, new PacketSerializer(buf));
                    } finally {
                        buf.release();
                    }
                });
            });
        }
    }

    @Override
    public void sendPacket(HypixelPacket packet) {
        if (developerConfig.devMode) Chat.sendPrivateMessageToSelfDebug("HP-Mod-API-Send: " + packet.getIdentifier());
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(new PacketSerializer(buf));
        ClientPlayNetworking.send(new Identifier(packet.getIdentifier()), buf);
    }

    @Override
    public void sendPacket(String identifier) {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(new Identifier(identifier), buf);
    }
}