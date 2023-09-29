package de.hype.bbsentials.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ColoredBoxRenderer {
    private static final float SIZE = 2.0f; // Adjust the size of the box if needed

    public static void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int red, int green, int blue, int alpha) {
        // Set the color for the box
        float r = red / 255.0F;
        float g = green / 255.0F;
        float b = blue / 255.0F;
        float a = alpha / 255.0F;

        matrixStack.push(); // Save the current transformation matrix

        // Apply translation to (100, 100, 100)
        matrixStack.translate(100.0, 100.0, 100.0);

        // Create a model transformation matrix
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        // Define vertices for a colored box
        float x1 = -SIZE;
        float y1 = -SIZE;
        float z1 = -SIZE;
        float x2 = SIZE;
        float y2 = SIZE;
        float z2 = SIZE;

        // Render the colored box
        RenderLayer layer = RenderLayer.getSolid();
        net.minecraft.client.render.VertexConsumer vertexConsumer = vertexConsumers.getBuffer(layer);
        vertexConsumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x2, y1, z1).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x2, y2, z1).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x1, y2, z1).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x1, y1, z2).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x2, y1, z2).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).light(1).next();
        vertexConsumer.vertex(matrix, x1, y2, z2).color(r, g, b, a).light(1).next();

        // Restore the previous transformation matrix
        matrixStack.pop();
    }


}
