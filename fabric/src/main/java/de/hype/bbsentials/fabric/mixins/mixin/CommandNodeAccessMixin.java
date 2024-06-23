package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.ICommandNodeMixinAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(CommandNode.class)
public abstract class CommandNodeAccessMixin<S> implements ICommandNodeMixinAccess<S> {
    @Final
    @Shadow(remap = false)
    private Map<String, CommandNode<S>> children;
    @Final
    @Shadow(remap = false)
    private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
    @Final
    @Shadow(remap = false)
    private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();

    @Shadow(remap = false)
    public abstract void addChild(CommandNode<S> node);

    @Unique
    @Override
    public Map<String, CommandNode<S>> BBsentials$getChildrenMap() {
        return children;
    }

    @Unique
    @Override
    public Map<String, LiteralCommandNode<S>> BBsentials$getLiterals() {
        return literals;
    }

    @Unique
    @Override
    public Map<String, ArgumentCommandNode<S, ?>> BBsentials$getArguments() {
        return arguments;
    }

    @Unique
    @Override
    public CommandNode<S> BBsentials$removeNode(String name) {
        BBsentials$getArguments().remove(name);
        BBsentials$getLiterals().remove(name);
        return BBsentials$getChildrenMap().remove(name);
    }
}
