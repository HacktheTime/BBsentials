package de.hype.bbsentials.forge.CommandImplementations;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.common.client.BBsentials.connection;


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
        connection.sendMessage("?dwevent bettertogether");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
