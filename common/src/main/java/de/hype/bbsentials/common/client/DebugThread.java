package de.hype.bbsentials.common.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DebugThread implements Runnable {

    @Override
    public void run() {
         loop();
        //place a breakpoint for only this thread here.
    }

    public void loop() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public void renderTextAtCoordinates(String text, double x, double y, double z) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Text renderedText = Text.of(text);

        MatrixStack matrices = new MatrixStack(); // Create a new MatrixStack

        matrices.push();
        matrices.translate(x, y, z);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int textColor = 0xFFFFFF; // Default color: white
//        public int draw(     String text,
//        float x,
//        float y,
//        int color,
//        boolean shadow,
//        Matrix4f matrix,
//        VertexConsumerProvider vertexConsumers,
//        TextRenderer.TextLayerType layerType,
//        int backgroundColor,
//        int light )
        textRenderer.draw(text,0f,0f,textColor,false,matrix4f,, textRenderer, Color.RED.getRGB(),10); // Default color: white

        matrices.pop();
    }

}
