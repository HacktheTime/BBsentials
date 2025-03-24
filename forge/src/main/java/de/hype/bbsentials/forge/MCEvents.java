package de.hype.bingonet.forge;

import de.hype.bingonet.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.client.common.mclibraries.Utils;
import de.hype.bingonet.shared.constants.Islands;
import de.hype.bingonet.shared.objects.Position;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MCEvents implements de.hype.bingonet.client.common.mclibraries.MCEvents {
    static Utils utils;

    @Override
    public void registerOverlays() {
        FMLCommonHandler.instance().bus().register(this);
        utils = EnvironmentCore.utils;
        FMLCommonHandler.instance().bus().register(utils);
    }

    @Override
    public void registerUseClick() {

    }

    @Override
    public void registerWaypoints() {

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            BlockPos pos = event.pos;
            if (pos == null) return;
            World world = event.world;
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockChest) {
                if (utils.getCurrentIsland().equals(Islands.CRYSTAL_HOLLOWS)) {
                    UpdateListenerManager.chChestUpdateListener.addOpenedChest(new Position(pos.getX(), pos.getY(), pos.getZ()));
                }
            }
        } catch (Exception ignored) {

        }
    }
}
