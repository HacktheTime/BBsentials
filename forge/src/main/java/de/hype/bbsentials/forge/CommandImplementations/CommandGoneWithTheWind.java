package de.hype.bbsentials.forge.CommandImplementations;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.common.client.BBsentials.connection;


public class CommandGoneWithTheWind extends CommandBase {

    @Override
    public String getCommandName() {
        return "gonewiththewind";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gonewiththewind";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        connection.sendMessage("?dwevent gonewiththewind");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
