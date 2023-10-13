package de.hype.bbsentials.forge.client.Commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.forge.client.BBsentials.bbserver;


public class CommandRaffle extends CommandBase {

    @Override
    public String getCommandName() {
        return "raffle";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/raffle";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        bbserver.sendMessage("?dwevent raffle");
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
