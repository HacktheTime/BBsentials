package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.shared.constants.VanillaBlocks;
import de.hype.bbsentials.shared.constants.VanillaEntities;
import de.hype.bbsentials.shared.objects.MinecraftEntity;
import de.hype.bbsentials.shared.objects.Position;

import java.util.List;
import java.util.function.Predicate;

public interface WorldUtils {
    List<MinecraftEntity> getEntities(VanillaEntities vanillaEntities);

    boolean isBlockAir(Position position);

    Position getPlayerPosEyeHightAdjusted();

    boolean isBlock(Position position,VanillaBlocks... blocks);
    boolean isBlockPredicate(Position position, Predicate<VanillaBlocks>... blockPredicates);
}
