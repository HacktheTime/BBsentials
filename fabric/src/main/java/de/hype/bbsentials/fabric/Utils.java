package de.hype.bbsentials.fabric;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.updatelisteners.ChChestUpdateListener;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.EnumUtils;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.ChChestData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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


    public boolean isWindowFocused() {
        return MinecraftClient.getInstance().isWindowFocused();
    }

    public File getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().toFile();
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
            boolean doPants = BBsentials.config.showMusicPants;
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
                Date warpClosingDate = new Date(360000 - (EnvironmentCore.utils.getLobbyTime() * 50));
                toRender.add(Text.of("§6Closing in " + warpClosingDate.getHours() + "h | " + warpClosingDate.getMinutes() + "m"));
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
        return MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry("!C-c").getDisplayName().getString().replace("Server:", "").trim();
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

    public long getLobbyTime() {
        return MinecraftClient.getInstance().world.getLevelProperties().getTime();
    }

}