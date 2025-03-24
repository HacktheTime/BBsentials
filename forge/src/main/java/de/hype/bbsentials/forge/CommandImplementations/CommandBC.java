package de.hype.bingonet.forge.CommandImplementations;


import de.hype.bingonet.shared.packets.network.BingoChatMessagePacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bingonet.client.common.client.BingoNet.connection;

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

        String message = String.join(" ",args);
        connection.sendPacket(new BingoChatMessagePacket("", "", message, 0));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
