package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.common.client.BBsentials;
import net.minecraftforge.client.ClientCommandHandler;

import static de.hype.bbsentials.common.client.BBsentials.getConfig;


public class CommandsOLD {
    public CommandsOLD() {
        ClientCommandHandler.instance.registerCommand(new CommandGoblinRaid());
        ClientCommandHandler.instance.registerCommand(new Command2xPowder());
        ClientCommandHandler.instance.registerCommand(new CommandBetterTogether());
        ClientCommandHandler.instance.registerCommand(new CommandRaffle());
        ClientCommandHandler.instance.registerCommand(new CommandGoneWithTheWind());
        ClientCommandHandler.instance.registerCommand(new CommandChChest());
        ClientCommandHandler.instance.registerCommand(new CommandBBServer());
        ClientCommandHandler.instance.registerCommand(new CommandBC());
        ClientCommandHandler.instance.registerCommand(new CommandBingoChat());
        if (getConfig().bbsentialsRoles != null) {
            if (getConfig().hasBBRoles("mod")) {
                ClientCommandHandler.instance.registerCommand(new CommandBAnnounce());
                ClientCommandHandler.instance.registerCommand(new CommandBMute());
                ClientCommandHandler.instance.registerCommand(new CommandBBan());
            }
            if (getConfig().hasBBRoles("splasher")) {
                ClientCommandHandler.instance.registerCommand(new CommandSplashAnnounce());
            }
            else {
            }
        }
    }

    public void sendCommand(String message) {
        BBsentials.connection.sendCommand(message);
    }
}

