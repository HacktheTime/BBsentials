package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import de.hype.bingonet.shared.constants.Islands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.spongepowered.asm.mixin.Unique;

public interface IBingoNetCommandSource extends FabricClientCommandSource {
    @Unique
    Islands BingoNet$getCurrentIsland();
}
