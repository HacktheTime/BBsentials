package de.hype.bbsentials.fabric;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.hype.bbsentials.common.chat.Chat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
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
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance
                .master(SoundEvent.of(new Identifier(eventName)), 1.0F, 1.0F));
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

    public List<String> getBingoPlayers() {
        List<String> bingoPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerListEntry entry : MinecraftClient.getInstance().player.networkHandler.getPlayerList()) {
            try {
                if (entry.getProfile().getName().startsWith("!")) {
                    String customName = entry.getDisplayName().getString();
                    if (customName.contains("Ⓑ")) {
                        bingoPlayers.add(customName.trim().split(" ")[1]);
                    }
                }
            } catch (Exception ignored) {
            }

        }
        return bingoPlayers;
    }

    public List<String> getIronmanPlayers() {
        List<String> ironmanPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (PlayerListEntry entry : MinecraftClient.getInstance().player.networkHandler.getPlayerList()) {
            try {
                if (entry.getProfile().getName().startsWith("!")) {
                    String customName = entry.getDisplayName().getString();
                    if (customName.contains("♻")) {
                        ironmanPlayers.add(customName.trim().split(" ")[1]);
                    }
                }
            } catch (Exception ignored) {
            }

        }
        return ironmanPlayers;
    }

    public List<PlayerEntity> onlyFromList(List<PlayerEntity> players, List<String> usernames) {
        ArrayList<PlayerEntity> filtered = new ArrayList<>();
        for (PlayerEntity player : players) {
            String playerUsername = player.getGameProfile().getName();
            for (int i = 0; i < usernames.size(); i++) {
                if (playerUsername.equals(usernames.get(i))) {
                    usernames.remove(i);
                    filtered.add(player);
                }
            }
        }
        return filtered;
    }

    public List<PlayerEntity> filterOut(List<PlayerEntity> players, List<String> usernames) {
        ArrayList<PlayerEntity> filtered = new ArrayList<>();
        for (PlayerEntity player : players) {
            String playerUsername = player.getGameProfile().getName();
            boolean toAdd = true;
            for (int i = 0; i < usernames.size(); i++) {
                if (playerUsername.equals(usernames.get(i))) {
                    toAdd = false;
                    usernames.remove(i);
                    break;
                }
            }
            if (toAdd) {
                filtered.add(player);
            }
        }
        return filtered;
    }

    public List<String> getSplashLeechingPlayers() {
        List<PlayerEntity> players = getAllPlayers();
        players.remove(MinecraftClient.getInstance().player);
        return getPlayersInRadius(MinecraftClient.getInstance().player, filterOut(getAllPlayers(), getBingoPlayers()), 5).stream().map((playerEntity -> playerEntity.getDisplayName().getString())).toList();
    }
}