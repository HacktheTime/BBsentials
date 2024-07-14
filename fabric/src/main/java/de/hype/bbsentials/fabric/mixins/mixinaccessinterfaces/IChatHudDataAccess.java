package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

public interface IChatHudDataAccess {
    @Unique
    ChatHudLine BBsentials$removeLine(int position);

    @Unique
    void BBsentials$removeBottomLines(int count);

    @Unique
    List<ChatHudLine> BBsentials$getMessageList();
}
