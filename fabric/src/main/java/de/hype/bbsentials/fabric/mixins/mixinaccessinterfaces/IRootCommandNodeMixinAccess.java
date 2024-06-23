package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface IRootCommandNodeMixinAccess<S> {
    @Unique
    void BBsentials$replaceNodes(List<LiteralArgumentBuilder<IBBsentialsCommandSource>> newNodes);
}
