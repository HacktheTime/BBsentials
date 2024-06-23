package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

public interface ICommandNodeMixinAccess<S> {
    @Unique
    Map<String, CommandNode<S>> BBsentials$getChildrenMap();

    @Unique
    Map<String, LiteralCommandNode<S>> BBsentials$getLiterals();

    @Unique
    Map<String, ArgumentCommandNode<S, ?>> BBsentials$getArguments();

    @Unique
    CommandNode<S> BBsentials$removeNode(String name);
}
