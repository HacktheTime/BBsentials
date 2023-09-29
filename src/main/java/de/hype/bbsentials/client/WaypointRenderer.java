package de.hype.bbsentials.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

;
public class WaypointRenderer {
    private final WaypointManager waypointManager;

    public WaypointRenderer(WaypointManager waypointManager) {
        this.waypointManager = waypointManager;

        // Register a callback to render waypoints on each frame.
        ClientTickEvents.END_CLIENT_TICK.register(this::renderWaypoints);
    }

    private void renderWaypoints(MinecraftClient client) {
        // Create a MatrixStack for transformations.
        MatrixStack matrixStack = new MatrixStack();

        // Start rendering in the game world.
        VertexConsumerProvider.Immediate buffer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderLayer.getSolid());

        for (Waypoint waypoint : waypointManager.getWaypoints()) {
            // Check if the waypoint should be rendered based on the player's dimension.
            if (client.player != null && BBUtils.getCurrentIsland() == waypoint.getIsland()) {
                double playerX = client.player.getX();
                double playerY = client.player.getY();
                double playerZ = client.player.getZ();

                double renderX = waypoint.getPosition().getX() - playerX;
                double renderY = waypoint.getPosition().getY() - playerY;
                double renderZ = waypoint.getPosition().getZ() - playerZ;

                // Define the box vertices for the waypoint.
                // Make sure to provide data for all eight corners.
                double x1 = renderX - 1.0;
                double y1 = renderY - 1.0;
                double z1 = renderZ - 1.0;
                double x2 = renderX + 1.0;
                double y2 = renderY + 1.0;
                double z2 = renderZ + 1.0;
                // Render the waypoint box.
//                WorldRenderer.drawBox(
//                        matrixStack,
//                        vertexConsumer,
//                        x1, y1, z1,    // Front top-left
//                        x2, y1, z1,    // Front top-right
//                        x2, y2, z1,    // Front bottom-right
//                        x1, y2, z1,    // Front bottom-left
//                        x1, y1, z2,    // Back top-left
//                        x2, y1, z2,    // Back top-right
//                        x2, y2, z2,    // Back bottom-right
//                        x1, y2, z2     // Back bottom-left
//                );

                // Pop the transformation matrices.
                ColoredBoxRenderer.render(matrixStack, MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(), 255, 0, 0, 255);
            }
        }

        // End rendering and draw the waypoints.
        buffer.draw();
    }

}
