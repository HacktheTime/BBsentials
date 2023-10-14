package de.hype.bbsentials.forge.CommandImplementations;


import de.hype.bbsentials.common.packets.packets.BingoChatMessagePacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bbsentials.common.client.BBsentials.connection;

public class CommandBC extends CommandBase {

    @Override
    public String getCommandName() {
        return "bc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bc <Message to Bingo Chat>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bc <Message to Bingo Chat>"));
            return;
        }

        String message = args[0];
        connection.sendPacket(new BingoChatMessagePacket("","",message,0));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
