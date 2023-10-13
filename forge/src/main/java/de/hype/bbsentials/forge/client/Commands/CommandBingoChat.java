package de.hype.bbsentials.forge.client.Commands;

import de.hype.bbsentials.forge.chat.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandBingoChat extends CommandBase {

    @Override
    public String getCommandName() {
        return "bingochat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bingochat <Message to Bingo Chat>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bingochat <Message to Bingo Chat>"));
            return;
        }

        String message = args[0];
        Chat.sendCommand("?bingochat " + message);
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
