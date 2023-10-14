package de.hype.bbsentials.forge.CommandImplementations;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.common.client.BBsentials.connection;


public class Command2xPowder extends CommandBase {

    @Override
    public String getCommandName() {
        return "2xpowder";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/2xpowder";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        connection.sendMessage("?dwevent 2xpowder");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
