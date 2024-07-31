package de.hype.bbsentials.fabric

import de.hype.bbsentials.client.common.mclibraries.WorldUtils
import de.hype.bbsentials.shared.constants.VanillaBlocks
import de.hype.bbsentials.shared.constants.VanillaEntities
import de.hype.bbsentials.shared.objects.MinecraftEntity
import de.hype.bbsentials.shared.objects.Position
import net.minecraft.client.MinecraftClient
import java.util.function.Predicate

class FabricWorldUtils : WorldUtils {
    var client: MinecraftClient = MinecraftClient.getInstance()
    override fun getEntities(type: VanillaEntities): MutableList<MinecraftEntity?>? {
        try {
            val baseId = VanillaRegistry.get(type)
            return client.world?.entities
                ?.filter { e -> e.type.untranslatedName == baseId }
                ?.map { it.getVanillaEntity() }
                ?.toMutableList() // Convert to MutableList
        }catch (e : Exception){
            e.printStackTrace()
        }
        return mutableListOf()
    }


    override fun isBlockAir(position: Position): Boolean {
        return client.world?.getBlockState(position.toBlockPos())?.isAir ?: false
    }

    override fun getPlayerPosEyeHightAdjusted(): Position? {
        return client.player?.eyePos?.toPos()
    }

    override fun isBlock(position: Position?, vararg blocks: VanillaBlocks?): Boolean {
        val javaClass = position?.toBlockPos()?.let { client.world?.getBlockState(it)?.javaClass }
        blocks.forEach {
            if (javaClass?.equals(VanillaRegistry.get(it)) == true) return true;
        }
        return false;
    }

    override fun isBlockPredicate(position: Position?, vararg blockPredicates: Predicate<VanillaBlocks>?): Boolean {
        val block =VanillaRegistry.get(position?.toBlockPos()?.let { client.world?.getBlockState(it)?.block })
        blockPredicates.forEach {
            if (it?.test(block) == true) return true
        }
        return false;
    }

}
