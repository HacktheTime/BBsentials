package de.hype.bbsentials.forge;

import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.Utils;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.Position;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MCEvents implements de.hype.bbsentials.client.common.mclibraries.MCEvents {
    static Utils utils = EnvironmentCore.utils;

    @Override
    public void registerOverlays() {
        FMLCommonHandler.instance().bus().register(utils);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void registerUseClick() {

    }

    @Override
    public void registerWaypoints() {

    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        World world = event.world;
        BlockPos pos = event.pos;
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockChest) {
            if (utils.getCurrentIsland().equals(Islands.CRYSTAL_HOLLOWS)) {
                UpdateListenerManager.chChestUpdateListener.addOpenedChest(new Position(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }
}
