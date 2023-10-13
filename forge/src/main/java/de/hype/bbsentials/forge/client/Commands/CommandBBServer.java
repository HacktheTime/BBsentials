package de.hype.bbsentials.forge.client.Commands;

import de.hype.bbsentials.forge.client.BBsentials;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandBBServer extends CommandBase {

    @Override
    public String getCommandName() {
        return "bbserver";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bbserver <Message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbserver <Message>"));
            return;
        }

        String message = args[0];
        if (message.equals("bb:reconnect")) {
            BBsentials.connectToBBserver();
        }
        else {
            BBsentials.bbserver.sendMessage(message);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
