package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandNode.class)
public abstract class CommandSourceMixin<S> implements Comparable<CommandNode<S>> {
//    @Shadow
//    private Map<String, CommandNode<S>> children;
//
//    @Shadow
//    private Map<String, LiteralCommandNode<S>> literals;
//
//    @Shadow
//    private Map<String, ArgumentCommandNode<S, ?>> arguments;
//
//    @Unique
//    public void removeChild(final CommandNode<S> node) {
//        final CommandNode<S> child = children.get(node.getName());
//        children.remove(child);
//        literals.remove(node.getName());
//        arguments.remove(node.getName());
//    }
//    @Shadow
//    public abstract Collection<CommandNode<S>> getChildren();

}