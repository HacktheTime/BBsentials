package de.hype.bbsentials.forge.client.Commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.forge.client.BBsentials.bbserver;


public class CommandBetterTogether extends CommandBase {

    @Override
    public String getCommandName() {
        return "bettertogether";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bettertogether";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        bbserver.sendMessage("?dwevent bettertogether");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
