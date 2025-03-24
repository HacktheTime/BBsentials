package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface IChatHudDataAccess {
    @Unique
    ChatHudLine BingoNet$removeLine(int position);

    @Unique
    void BingoNet$removeBottomLines(int count);

    @Unique
    List<ChatHudLine> BingoNet$getMessageList();
}
