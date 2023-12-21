package de.hype.bbsentials.forge.CommandImplementations;


import de.hype.bbsentials.shared.packets.network.PunishUserPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bbsentials.forge.Commands.sendPacket;

public class CommandBBan extends CommandBase {

    @Override
    public String getCommandName() {
        return "bban";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <userId/mcusername> <[Duration(d/h/m/s) | 0 forever]> <Reason>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            String message = String.join(" ", args).trim();
            String identification = message.split(" ",3)[0];
            String duration =message.split(" ",3)[1];
            String reason = message.split(" ",3)[3];
            int userId = -1;
            String mcusername = "";
            if (identification.replaceAll("[\\d]", "").trim().isEmpty()) {
                userId = Integer.parseInt(identification);
            }
            else {
                mcusername = identification;
            }
            sendPacket(new PunishUserPacket(PunishUserPacket.PUNISHMENT_TYPE_MUTE, userId, mcusername, duration, reason));        }
        else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
