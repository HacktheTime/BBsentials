package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IBBsentialsCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(RootCommandNode.class)
public abstract class RootCommandNodeAccessMixin<S> extends CommandNodeAccessMixin<S> implements de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IRootCommandNodeMixinAccess<S> {

    @Unique
    @Override
    public void BBsentials$replaceNodes(List<LiteralArgumentBuilder<IBBsentialsCommandSource>> newNodes) {
        for (LiteralArgumentBuilder<IBBsentialsCommandSource> newNode : newNodes) {
            if (BBsentials$removeNode(newNode.getLiteral()) != null){
                addChild((CommandNode<S>) (Object) newNode.build());
            }
        }
    }
}

