package de.hype.bingonet.forge.CommandImplementations;

import de.hype.bingonet.shared.packets.network.BroadcastMessagePacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bingonet.client.common.client.BingoNet.connection;

public class CommandBAnnounce extends CommandBase {

    @Override
    public String getCommandName() {
        return "bannounce";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            String message = String.join(" ", args);
            connection.sendPacket(new BroadcastMessagePacket("","",message));
        }
        else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
