package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

public interface ICommandNodeMixinAccess<S> {
    @Unique
    Map<String, CommandNode<S>> BingoNet$getChildrenMap();

    @Unique
    Map<String, LiteralCommandNode<S>> BingoNet$getLiterals();

    @Unique
    Map<String, ArgumentCommandNode<S, ?>> BingoNet$getArguments();

    @Unique
    CommandNode<S> BingoNet$removeNode(String name);
}
