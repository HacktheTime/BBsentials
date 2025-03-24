package de.hype.bingonet.forge.CommandImplementations;

import de.hype.bingonet.client.common.chat.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;


public class CommandChChest extends CommandBase {

    @Override
    public String getCommandName() {
        return "chchest";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Disabled on 1.8.9 due too missing autocompletion. Please use Discord / Modern";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Chat.sendPrivateMessageToSelfError("Disabled on 1.8.9 due too missing autocompletion. Please use Discord / Modern");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
