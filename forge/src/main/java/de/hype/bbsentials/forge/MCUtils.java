package de.hype.bbsentials.forge;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.hype.bbsentials.common.client.BBsentials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
    public static boolean isBingo(EntityPlayer player) {
        try {
            return player.getDisplayNameString().contains("Ⓑ");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIronman(EntityPlayer player) {
        try {
            return player.getDisplayNameString().contains("♻");
        } catch (Exception e) {
            return false;
        }
    }

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

        // Iterate through all players and check their distance from the source player
        for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (!player.getDisplayNameString().startsWith("!")) {
                if (Pattern.compile("§(?!f)\\w+").matcher(IChatComponent.Serializer.componentToJson(player.getDisplayName())).find()) {
                    players.add(player);
                }
            }
        }

        return players;
    }

    public boolean isInRadius(EntityPlayer referencePlayer, EntityPlayer player, double radius) {
        return player != referencePlayer && player.getDistanceSq(referencePlayer.posX, referencePlayer.posY, referencePlayer.posZ) <= radius * radius;
    }

    public List<EntityPlayer> filterOut(List<EntityPlayer> players, Predicate<EntityPlayer> predicate) {
        return players.stream().filter(predicate).collect(Collectors.toList());
    }

    private List<EntityPlayer> getSplashLeechingPlayersPlayerEntity() {
        List<EntityPlayer> players = getAllPlayers();
        players.remove(Minecraft.getMinecraft().thePlayer);
        return filterOut(filterOut(getAllPlayers(), (player -> !isBingo(player))), (player) -> isInRadius(Minecraft.getMinecraft().thePlayer, player, 5));
    }

    public List<String> getSplashLeechingPlayers() {
        return getSplashLeechingPlayersPlayerEntity().stream().map((player -> player.getDisplayName().getFormattedText())).collect(Collectors.toList());
    }


    @SubscribeEvent
    public void renderSplashOverlay(RenderGameOverlayEvent.Text event) {
        if (!BBsentials.splashStatusUpdateListener.showSplashOverlay()) return;

        // Set the starting position for the overlay
        int x = 10;
        int y = 10;

        // Render each string in the list
        List<EntityPlayer> splashLeechers = getSplashLeechingPlayersPlayerEntity();
        List<EntityPlayer> allParticipants = filterOut(getAllPlayers(), (player) -> isInRadius(Minecraft.getMinecraft().thePlayer, player, 5));
        List<EntityPlayer> musicPants = new ArrayList<>();

        List<IChatComponent> toDisplay = new ArrayList<>();
        toDisplay.add(new ChatComponentText("§6Total: " + allParticipants.size() + " | Bingos: " + (allParticipants.size() - splashLeechers.size()) + " | Leechers: " + splashLeechers.size()));
        boolean doPants = BBsentials.config.showMusicPants;
        for (EntityPlayer participant : allParticipants) {
            if (doPants) {
                boolean hasPants = false;
                for (ItemStack armorItem : participant.inventory.armorInventory) {
                    try {
                        if (armorItem.getTagCompound().getCompoundTag("ExtraAttributes").getString("display").contains("MUSIC_PANTS")) {
                            musicPants.add(participant);
                            hasPants = true;
                        }
                    } catch (Exception ignored) {
                        continue;
                    }
                }
                if (hasPants) {
                    String pantsAddition = IChatComponent.Serializer.componentToJson(new ChatComponentText("§4[♪]§ "));
                    String normal = IChatComponent.Serializer.componentToJson(participant.getDisplayName());
                    toDisplay.add(IChatComponent.Serializer.jsonToComponent("[" + pantsAddition + "," + normal + "]"));
                }
            }
        }
        toDisplay.addAll(splashLeechers.stream().map(EntityPlayer::getDisplayName).collect(Collectors.toList()));
        for (IChatComponent text : toDisplay) {
            Minecraft.getMinecraft().fontRendererObj.drawString(text.getFormattedText(), x, y, 0xFFFFFF);
            y += 10; // Adjust the vertical position for the next string
        }
    }

    @Override
    public void registerSplashOverlay() {
        FMLCommonHandler.instance().bus().register(this);
    }

}
