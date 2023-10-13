package de.hype.bbsentials.forge.client.Commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.forge.client.BBsentials.bbserver;


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
        bbserver.sendMessage("?dwevent 2xpowder");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
