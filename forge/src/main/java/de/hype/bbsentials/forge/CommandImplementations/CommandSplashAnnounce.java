package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.client.common.chat.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandSplashAnnounce extends CommandBase {

    @Override
    public String getCommandName() {
        return "splashAnnounce";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Disabled on 1.8.9 due too missing autocompletion. Please use Discord / 1.20.2";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Chat.sendPrivateMessageToSelfError("Disabled on 1.8.9 due too missing autocompletion. Please use Discord / 1.20.2");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
