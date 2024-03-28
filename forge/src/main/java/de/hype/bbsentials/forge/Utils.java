package de.hype.bbsentials.forge;

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
import de.hype.bbsentials.shared.objects.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils implements de.hype.bbsentials.client.common.mclibraries.Utils {
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

    public static String getTabListPlayerName(String id) {
        return Minecraft.getMinecraft().getNetHandler().getPlayerInfo(id).getDisplayName().getUnformattedText();
    }

    public boolean isWindowFocused() {
        return Display.isActive();
    }

    public File getConfigPath() {
        File configDir = Minecraft.getMinecraft().mcDataDir;
        File bbsentialsDir = new File(configDir, "BBsentials");

        // Create the folder if it doesn't exist
        if (!bbsentialsDir.exists()) {
            boolean created = bbsentialsDir.mkdirs();
            if (!created) {
                // Handle the case where folder creation fails
                throw new RuntimeException("Failed to create BBsentials folder");
            }
        }

        return bbsentialsDir;
    }

    public String getUsername() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public String getMCUUID() {
        return Minecraft.getMinecraft().getSession().getPlayerID().toString();
    }

    public void playsound(String eventName) {
        if (eventName.isEmpty()) Minecraft.getMinecraft().getSoundHandler().stopSounds();
        else
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

    public InputStream makeScreenshot() {
        Minecraft mc = Minecraft.getMinecraft();

        int width = mc.displayWidth;
        int height = mc.displayHeight;

        // Create ByteBuffer to hold the pixel data
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Create BufferedImage from pixel data
        BufferedImage screenshotImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 4;
                int r = buffer.get(index) & 0xFF;
                int g = buffer.get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                int a = buffer.get(index + 3) & 0xFF;
                screenshotImage.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        // Convert BufferedImage to ByteArrayInputStream
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshotImage, "png", outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getStringFromTextJson(String textJson) throws Exception {
        return IChatComponent.Serializer.jsonToComponent(textJson).getUnformattedText();
    }

    @Override
    public boolean executeClientCommand(String command) {
        return false;
    }

    @Override
    public boolean isJsonParseableToText(String json) {
        try {
            IChatComponent.Serializer.jsonToComponent(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String stringToTextJson(String string) {
        if (isJsonParseableToText(string)) return string;
        return IChatComponent.Serializer.componentToJson(new ChatComponentText(string));
    }

    @Override
    public Position getPlayersPosition() {
        BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();
        return new Position(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void systemExit(int id) {
        FMLCommonHandler.instance().exitJava(id, false);
    }

    @Override
    public boolean isInGame() {
        return Minecraft.getMinecraft().thePlayer != null;
    }

    @Override
    public void showErrorScreen(String s) {

    }

    @Override
    public boolean isSelfBingo() {
        return Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().contains("Ⓑ");
    }

    @Override
    public List<String> getPlayers() {
        return getAllPlayers().stream().map((playerEntity) -> playerEntity.getDisplayName().getFormattedText()).collect(Collectors.toList());
    }

    @SubscribeEvent
    public void renderOverlays(RenderGameOverlayEvent.Text event) {
        if (UpdateListenerManager.splashStatusUpdateListener.showOverlay()) {

            // Set the starting position for the overlay
            int x = 10;
            int y = 10;

            // Render each string in the list
            List<EntityPlayer> splashLeechers = getSplashLeechingPlayersPlayerEntity();
            List<EntityPlayer> allParticipants = filterOut(getAllPlayers(), (player) -> isInRadius(Minecraft.getMinecraft().thePlayer, player, 5));
            List<EntityPlayer> musicPants = new ArrayList<>();

            List<IChatComponent> toDisplay = new ArrayList<>();
            toDisplay.add(new ChatComponentText("§6Total: " + allParticipants.size() + " | Bingos: " + (allParticipants.size() - splashLeechers.size()) + " | Leechers: " + splashLeechers.size()));
            boolean doPants = BBsentials.splashConfig.showMusicPantsUsers;
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
        if (UpdateListenerManager.chChestUpdateListener.showOverlay()) {
            ChChestUpdateListener listener = UpdateListenerManager.chChestUpdateListener;

            int x = 10;
            int y = 15;
            List<IChatComponent> toRender = new ArrayList<>();
            if (listener.isHoster) {
                String status = listener.lobby.getStatus();
                switch (status) { // Fix switch statement
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

                toRender.add(new ChatComponentText("§6Status: §0" + status + "§6 | Slots: " + warpInfo + "§6"));
                Date warpClosingDate = new Date(408000 - (EnvironmentCore.utils.getLobbyTime() * 50));
                toRender.add(new ChatComponentText("§6Closing in " + warpClosingDate.getHours() + "h ," + warpClosingDate.getMinutes() + "m"));
            }
            else {
                toRender.add(new ChatComponentText("§4Please Leave the Lobby after getting all the Chests to allow people to be warped in!"));
                for (ChChestData chest : listener.getUnopenedChests()) {
                    String author = "";
                    if (!listener.lobby.contactMan.equalsIgnoreCase(chest.finder)) author = " [" + chest.finder + "]";
                    toRender.add(new ChatComponentText("(" + chest.coords.toString() + ")" + author + ":")); // Fix stream to array
                    Arrays.stream(chest.items).map(ChChestItem::getDisplayName).forEach((string) -> toRender.add(new ChatComponentText(string)));
                }
            }
            for (IChatComponent text : toRender) {
                Minecraft.getMinecraft().fontRendererObj.drawString(text.getFormattedText(), x, y, 0xFFFFFF);
                y += 10; // Adjust the vertical position for the next string
            }
        }
    }

    public Islands getCurrentIsland() {
        try {
            String string = getTabListPlayerName("!C-b");
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
        return Integer.parseInt(getTabListPlayerName("!B-a").trim().replaceAll("[^0-9]", ""));
    }

    public String getServerId() {
        return getTabListPlayerName("!C-c").replace("Server:", "").trim();
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
    public long getLobbyTime() {
        return Minecraft.getMinecraft().theWorld.getWorldTime();
    }
}
