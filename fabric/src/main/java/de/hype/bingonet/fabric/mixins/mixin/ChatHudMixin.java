package de.hype.bingonet.fabric.mixins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.IChatHudDataAccess;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public class ChatHudMixin implements IChatHudDataAccess {
    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @ModifyExpressionValue(method = "addVisibleMessage", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2))
    public int BingoNet$noChatRemoveVisibleMessage(int original) {
        if (BingoNet.visualConfig.infiniteChatHistory) return 0;
        return original;
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0))
    public int BingoNet$noChatRemoveMessage(int original) {
        if (BingoNet.visualConfig.infiniteChatHistory) return 0;
        return original;
    }

    @Unique
    @Override
    public ChatHudLine BingoNet$removeLine(int position) {
        return messages.remove(position);
    }

    @Unique
    @Override
    public void BingoNet$removeBottomLines(int count) {
        count = Math.min(count, messages.size());
        List<String> deleted = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String content = messages.removeFirst().content().getString();
            if (BingoNet.developerConfig.devMode) {
                deleted.add(content);
            }
        }
        for (int i = deleted.size() - 1; i >= 0; i--) {
            Chat.sendPrivateMessageToSelfError(deleted.get(i));
        }
    }

    @Unique
    @Override
    public List<ChatHudLine> BingoNet$getMessageList() {
        return messages;
    }
}
