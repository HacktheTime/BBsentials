package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.common.chat.Chat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bbsentials.common.client.BBsentials.connection;


public class CommandChChest extends CommandBase {

    @Override
    public String getCommandName() {
        return "chchest";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chchest <Item> <X> <Y> <Z> <ContactWay>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 5) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /chchest <Item> <X> <Y> <Z> <ContactWay>"));
            return;
        }

        String item = args[0];
        try {
            int x = parseInt(args[1]);
            int y = parseInt(args[2]);
            int z = parseInt(args[3]);
            String contactWay = args[4];

            String combinedString = "?chchest " + item + " " + x + " " + y + " " + z + " " + contactWay;
            connection.sendMessage(combinedString);
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError("Your coords were invalid.");
        }
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
