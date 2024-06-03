package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import de.hype.bbsentials.shared.constants.Islands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.spongepowered.asm.mixin.Unique;

public interface IBBsentialsCommandSource extends FabricClientCommandSource {
    @Unique
    Islands BBsentials$getCurrentIsland();
}
