package de.hype.bingonet.forge.CommandImplementations;

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
//        new MoulConfigManager().openConfigGui();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
