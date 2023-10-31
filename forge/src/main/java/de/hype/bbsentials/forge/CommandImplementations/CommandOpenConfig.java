package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.forge.ExampleMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;


public class CommandOpenConfig extends CommandBase {

    @Override
    public String getCommandName() {
        return "bbconfig";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bbconfig";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ExampleMod.config.openConfigGui();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
