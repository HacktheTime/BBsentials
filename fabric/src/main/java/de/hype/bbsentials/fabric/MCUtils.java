package de.hype.bbsentials.fabric;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.BBsentials;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
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
                if (Pattern.compile("\"color\":\"(?!white)\\w+\"").matcher(Text.Serializer.toJson(player.getDisplayName())).find()) {
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

    public void registerSplashOverlay() {
        HudRenderCallback.EVENT.register(this::renderSplashOverlay);
    }

    private void renderSplashOverlay(DrawContext drawContext, float v) {
        if (!BBsentials.splashStatusUpdateListener.showSplashOverlay()) return;
        // Set the starting position for the overlay
        int x = 10;
        int y = 10;

        // Render each string in the list
        List<PlayerEntity> splashLeechers = getSplashLeechingPlayersPlayerEntity();
        List<PlayerEntity> allParticipiants = filterOut(getAllPlayers(), (player) -> isInRadius(MinecraftClient.getInstance().player, player, 5));
        List<PlayerEntity> musicPants = new ArrayList<>();
        List<Text> toDisplay = new ArrayList<>();
        toDisplay.add(Text.of("§6Total: " + allParticipiants.size() + " | Bingos: " + (allParticipiants.size() - splashLeechers.size()) + " | Leechers: " + splashLeechers.size()));
        for (PlayerEntity participiant : allParticipiants) {
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
                String pantsAddition = Text.Serializer.toJson(Text.of("§4[♪]§ "));
                String normal = Text.Serializer.toJson(participiant.getDisplayName());
                toDisplay.add(Text.Serializer.fromJson("[" + pantsAddition + "," + normal + "]"));
            }
        }
        toDisplay.addAll(splashLeechers.stream().map(PlayerEntity::getDisplayName).toList());
        for (Text text : toDisplay) {
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
            y += 10; // Adjust the vertical position for the next string
        }
    }
}