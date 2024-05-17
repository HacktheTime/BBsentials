package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.config.constants.ClickableArmorStand;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IChestBlockEntityMixinAccess;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.Position;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MCEvents implements de.hype.bbsentials.client.common.mclibraries.MCEvents {
    public Utils utils = (Utils) EnvironmentCore.utils;

//    public static void renderWaypoints(WorldRenderContext context) {
//        Camera camera = context.camera();
//
//        MatrixStack matrixStack = new MatrixStack();
//
//        for (Waypoints waypoint : Waypoints.waypoints.values()) {
//            if (waypoint.visible) {
//                Vec3d waypointPosition = new Vec3d(waypoint.position.x, waypoint.position.y, waypoint.position.z);
//                double distance = camera.getPos().distanceTo(waypointPosition);
//                Vec3d transformedPosition = waypointPosition.subtract(camera.getPos());
//
//                matrixStack.push();
//                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
//                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
//                matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);
//
//                Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
//                Tessellator tessellator = Tessellator.getInstance();
//                BufferBuilder buffer = tessellator.getBuffer();
//
//                float size = (float) distance / 10;
//                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
//                buffer.vertex(positionMatrix, -size, size, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
//                buffer.vertex(positionMatrix, -size, -size, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
//                buffer.vertex(positionMatrix, size, -size, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
//                buffer.vertex(positionMatrix, size, size, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
//
//                RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
//                RenderSystem.setShaderTexture(0, new Identifier("bbsentials", "textures/item/prehistoric_egg.png"));
//                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//                RenderSystem.disableCull();
//                RenderSystem.depthFunc(GL11.GL_ALWAYS);
//
//                tessellator.draw();
//
//                RenderSystem.depthFunc(GL11.GL_LEQUAL);
//                RenderSystem.enableCull();
//
//                matrixStack.pop();
//
////                String distanceText = String.format("%.2f blocks", distance);
////                MinecraftClient.getInstance().textRenderer.draw(distanceText, 0, 0, 0xFFFFFF);
//            }
//        }
//    }

    public void registerOverlays() {
        utils = (Utils) EnvironmentCore.utils;
        HudRenderCallback.EVENT.register((obj1, obj2) -> utils.renderOverlays(obj1, obj2));
    }

    @Override
    public void registerUseClick() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            return onBlockClickInteraction(player, world, hand, hitResult);
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                    BBsentials.executionService.execute(() -> {
                        onArmorstandInteraction(player, world, hand, entity, hitResult);
                    });
                    return ActionResult.PASS;
                }
        );
    }

    private ActionResult onBlockClickInteraction(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (hitResult.getBlockPos() == null) return ActionResult.PASS;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() instanceof ChestBlock) {
                try {
                    if (utils.getCurrentIsland().equals(Islands.CRYSTAL_HOLLOWS)) {
                        UpdateListenerManager.chChestUpdateListener.addOpenedChest(new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                        IChestBlockEntityMixinAccess access = (IChestBlockEntityMixinAccess) world.getBlockEntity(blockPos);
                        //is it open already? if so ignore cause not new
                        boolean alreadyOpened = access.BBsentials$isOpen();
                        //schedule to check after processing to allow detecting whether chest is now opened → just opened
                        BBsentials.executionService.schedule(() -> {
                            if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
                            if (!access.BBsentials$isOpen() || alreadyOpened) return;
                            BBsentials.executionService.schedule(() -> {
                                //Check whether it still exists → filter for the powder chest with unfortunate timing
                                if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
                                Set<ChChestItem> items = BBsentials.temporaryConfig.chestParts;
                                items.clear();
                                BBsentials.executionService.schedule(() -> {
//                                    Chat.sendPrivateMessageToSelfDebug("Global Chest Detected");
                                    BBsentials.executionService.schedule(() -> {
                                        if (BBsentials.temporaryConfig.chestParts.isEmpty()) {
//                                            Chat.sendPrivateMessageToSelfDebug("Global Chest " + blockPos.toShortString() + ":" + " Nothing of value");
                                            return;
                                        }
                                        Position pos = new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                        Chat.sendPrivateMessageToSelfText(Message.tellraw("{\"text\":\"Global Chest Found ($coords): $items (Click here to announce)\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/chchest \\\"$items\\\" $coords \\\"/msg $username bb:party me\\\" Ⓐ\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"You remain responsible. The detection is not flawless and can be wrong\",\"color\":\"dark_red\"}]}}".replace("$coords", pos.toString()).replace("$username", BBsentials.generalConfig.getUsername()).replace("$items", String.join("2", items.stream().map(ChChestItem::getDisplayName).collect(Collectors.joining(";"))))));
                                    }, 2, TimeUnit.SECONDS);
                                }, 2, TimeUnit.SECONDS);
                            }, 1, TimeUnit.SECONDS);
                        }, 1, TimeUnit.SECONDS);

                    }
                } catch (Exception e) {
                    return ActionResult.PASS;
                }
            }
        }
        return ActionResult.PASS;
    }

    public void onArmorstandInteraction(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof ArmorStandEntity) {
            ((ArmorStandEntity) entity).getArmorItems().forEach(itemStack -> {
                if (itemStack.getItem() == Items.PLAYER_HEAD) {
//TODO                    String texture = itemStack.getNbt().getCompound("SkullOwner").getCompound("Properties").getList("textures", NbtElement.COMPOUND_TYPE).getCompound(0).getString("Value");
//                    ClickableArmorStand armorStand = ClickableArmorStand.getFromTexture(texture);
//                    if (armorStand != null) Chat.sendPrivateMessageToSelfSuccess(armorStand.toString()+" was clicked");
                    //TODO Maybe used for fairysouls here soon
                }
            });
        }
    }

    @Override
    public void registerWaypoints() {
//        WorldRenderEvents.END.register(MCEvents::renderWaypoints);
    }

}
