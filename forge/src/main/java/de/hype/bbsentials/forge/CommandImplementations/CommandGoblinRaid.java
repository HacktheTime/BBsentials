package de.hype.bbsentials.forge.CommandImplementations;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.common.client.BBsentials.connection;


public class CommandGoblinRaid extends CommandBase {

    @Override
    public String getCommandName() {
        return "goblinraid";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/goblinraid";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        connection.sendMessage("?dwevent goblinraid");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
