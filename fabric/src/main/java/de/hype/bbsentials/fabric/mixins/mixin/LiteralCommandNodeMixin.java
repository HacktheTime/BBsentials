package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Mixin(value = LiteralCommandNode.class, remap = false)
public abstract class LiteralCommandNodeMixin<S> extends CommandNode<S> {

    protected LiteralCommandNodeMixin(Command<S> command, Predicate<S> requirement, CommandNode<S> redirect, RedirectModifier<S> modifier, boolean forks) {
        super(command, requirement, redirect, modifier, forks);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;suggest(Ljava/lang/String;)Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;"), method = "listSuggestions", cancellable = true)
    public void listSuggestions(CommandContext<S> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        if (!getRequirement().test(context.getSource())) {
            cir.setReturnValue(Suggestions.empty());
            cir.cancel();
        }
    }

    @Shadow
    public abstract boolean isValidInput(String input);

    @Shadow
    public abstract String getName();

    @Shadow
    public abstract String getUsageText();

    @Shadow
    public abstract void parse(StringReader reader, CommandContextBuilder<S> contextBuilder) throws CommandSyntaxException;

    @Shadow
    public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException;

    @Shadow
    public abstract ArgumentBuilder<S, ?> createBuilder();

    @Shadow
    protected abstract String getSortedKey();

    @Shadow
    public abstract Collection<String> getExamples();
}
