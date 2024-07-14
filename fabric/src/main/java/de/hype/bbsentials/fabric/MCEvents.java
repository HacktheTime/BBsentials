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
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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

    public void registerOverlays() {
        utils = (Utils) EnvironmentCore.utils;
        HudRenderCallback.EVENT.register((obj1, obj2) -> utils.renderOverlays(obj1, obj2));
    }

    @Override
    public void registerUseClick() {
        UseBlockCallback.EVENT.register(this::onBlockClickInteraction);
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                    BBsentials.executionService.execute(() -> {
                        onArmorstandInteraction(player, world, hand, entity, hitResult, true);
                    });
                    return ActionResult.PASS;
                }
        );
        AttackEntityCallback.EVENT.register(((player, world, hand, entity, hitResult) -> {
            BBsentials.executionService.execute(() -> {
                onArmorstandInteraction(player, world, hand, entity, hitResult, false);
            });
            return ActionResult.PASS;
        }));
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
//                        BBsentials.executionService.schedule(() -> {
//                            if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
//                            if (!access.BBsentials$isOpen() || alreadyOpened) return;
//                            BBsentials.executionService.schedule(() -> {
//                                //Check whether it still exists → filter for the powder chest with unfortunate timing
//                                if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
//                                Set<ChChestItem> items = BBsentials.temporaryConfig.chestParts;
//                                items.clear();
//                                BBsentials.executionService.schedule(() -> {
////                                    Chat.sendPrivateMessageToSelfDebug("Global Chest Detected");
//                                    BBsentials.executionService.schedule(() -> {
//                                        if (BBsentials.temporaryConfig.chestParts.isEmpty()) {
////                                            Chat.sendPrivateMessageToSelfDebug("Global Chest " + blockPos.toShortString() + ":" + " Nothing of value");
//                                            return;
//                                        }
//                                        Position pos = new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
//                                        Chat.sendPrivateMessageToSelfText(Message.tellraw("{\"text\":\"Global Chest Found ($coords): $items (Click here to announce)\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/chchest \\\"$items\\\" $coords \\\"/msg $username bb:party me\\\" Ⓐ\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"You remain responsible. The detection is not flawless and can be wrong\",\"color\":\"dark_red\"}]}}".replace("$coords", pos.toString()).replace("$username", BBsentials.generalConfig.getUsername()).replace("$items", String.join("2", items.stream().map(ChChestItem::getDisplayName).collect(Collectors.joining(";"))))));
//                                    }, 2, TimeUnit.SECONDS);
//                                }, 2, TimeUnit.SECONDS);
//                            }, 1, TimeUnit.SECONDS);
//                        }, 1, TimeUnit.SECONDS);
                        BBsentials.executionService.schedule(() -> {
                            if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
                            if (!access.BBsentials$isOpen() || alreadyOpened) return;
                            BBsentials.executionService.schedule(() -> {
                                //Check whether it still exists → filter for the powder chest with unfortunate timing
                                if (!(world.getBlockEntity(blockPos) instanceof ChestBlockEntity)) return;
                                BBsentials.executionService.schedule(() -> {
                                    if (BBsentials.developerConfig.devMode){Chat.sendPrivateMessageToSelfDebug("Set the Global Chest Blockpos too %s".formatted(blockPos.toString()));}
                                    BBsentials.temporaryConfig.lastGlobalChchestCoords = new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ());
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

    /**
     * @param player
     * @param world
     * @param hand
     * @param entity
     * @param hitResult
     * @param use whether it is a punch or an use on the entity. if true it is an right click (use)
     */
    public void onArmorstandInteraction(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult, boolean use) {
        ModInitialiser.tutorialManager.clickedEntity(entity,use);
        if (entity instanceof ArmorStandEntity) {
            ((ArmorStandEntity) entity).getArmorItems().forEach(itemStack -> {
                if (itemStack.getItem() == Items.PLAYER_HEAD) {
                    ProfileComponent profileComponent = itemStack.get(DataComponentTypes.PROFILE);
                    if (profileComponent == null) return;
                    String texture = profileComponent.properties().get("textures").stream().toList().getFirst().value();
                    ClickableArmorStand armorStand = ClickableArmorStand.getFromTexture(texture);
//                    if (armorStand != null) Chat.sendPrivateMessageToSelfSuccess(armorStand.toString()+" was clicked");
                    //TODO Maybe used for fairysouls here soon
                }
            });
        }
    }

}
