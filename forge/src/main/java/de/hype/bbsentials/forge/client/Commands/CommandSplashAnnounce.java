package de.hype.bbsentials.forge.client.Commands;

import de.hype.bbsentials.forge.chat.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;

public class CommandSplashAnnounce extends CommandBase {

    @Override
    public String getCommandName() {
        return "splashAnnounce";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <Hub> <location> [extramessage]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 2) {
            int hub_Number = 0;
            try {
                hub_Number = parseInt(args[0], 1, 28);
            } catch (NumberInvalidException e) {
                Chat.sendPrivateMessageToSelf("§cInvalid hub number");
            }
            String location = args[1];
            String message = "";

            if (args.length > 2) {
                message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            }

            Chat.sendCommand("?splash " + hub_Number + " " + location + " " + message);
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
