package de.hype.bbsentials.forge;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
    public boolean isWindowFocused() {
        return Display.isActive();
    }

    public File getConfigPath() {
        return new File(Minecraft.getMinecraft().mcDataDir, "config");
    }

    public String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public String getMCUUID() {
        return Minecraft.getMinecraft().getSession().getPlayerID().toString();
    }


    public void playsound(String eventName) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(eventName), 1.0F, 1.0F, 0.0F));
    }

    public int getPotTime() {
        int remainingDuration = 0;
        PotionEffect potTimeRequest = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.damageBoost);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingDuration = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingDuration;
    }


    public String mojangAuth(String serverId) {
        try {
            Minecraft.getMinecraft().getSessionService().joinServer(Minecraft
                    .getMinecraft()
                    .getSession()
                    .getProfile(), Minecraft.getMinecraft().getSession().getToken(), serverId);
        } catch (AuthenticationException e) {
            return null;
        }
        return serverId;
    }

    public List<EntityPlayer> getAllPlayers() {
        List<EntityPlayer> players = new ArrayList<>();

        // Iterate through all players on the server
        for (EntityPlayer player : Minecraft.getMinecraft().thePlayer.getEntityWorld().playerEntities) {
            if (!player.getDisplayNameString().startsWith("!")) {
                players.add(player);
            }
        }

        return players;
    }

    public List<EntityPlayer> getPlayersInRadius(EntityPlayer referencePlayer, List<EntityPlayer> players, double radius) {
        List<EntityPlayer> nearbyPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the reference player
        for (EntityPlayer player : players) {
            if (player != referencePlayer && player.getDistanceSq(referencePlayer.posX, referencePlayer.posY, referencePlayer.posZ) <= radius * radius) {
                nearbyPlayers.add(player);
            }
        }

        return nearbyPlayers;
    }


    public List<String> getBingoPlayers() {
        List<String> bingoPlayers = new ArrayList<>();

        // Iterate through all players and check their distance from the source player
        for (Iterator<NetworkPlayerInfo> it = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().iterator(); it.hasNext(); ) {
            NetworkPlayerInfo entry = it.next();
            try {
                if (entry.getGameProfile().getName().startsWith("!")) {
                    String customName = entry.getDisplayName().getUnformattedText();
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
        for (Iterator<NetworkPlayerInfo> it = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().iterator(); it.hasNext(); ) {
            NetworkPlayerInfo entry = it.next();
            try {
                if (entry.getGameProfile().getName().startsWith("!")) {
                    String customName = entry.getDisplayName().getUnformattedText();
                    if (customName.contains("♻")) {
                        ironmanPlayers.add(customName.trim().split(" ")[1]);
                    }
                }
            } catch (Exception ignored) {
            }

        }
        return ironmanPlayers;
    }

    public List<EntityPlayer> onlyFromList(List<EntityPlayer> players, List<String> usernames) {
        ArrayList<EntityPlayer> filtered = new ArrayList<>();
        for (EntityPlayer player : players) {
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

    public List<EntityPlayer> filterOut(List<EntityPlayer> players, List<String> usernames) {
        ArrayList<EntityPlayer> filtered = new ArrayList<>();
        for (EntityPlayer player : players) {
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
        List<EntityPlayer> players = getAllPlayers();
        players.remove(Minecraft.getMinecraft().thePlayer);
        return getPlayersInRadius(Minecraft.getMinecraft().thePlayer, filterOut(getAllPlayers(), getBingoPlayers()), 5).stream().map((playerEntity -> playerEntity.getDisplayName().getFormattedText())).collect(Collectors.toList());
    }
}
