package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.mclibraries.MCCommand;
import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.packets.packets.SplashNotifyPacket;
import de.hype.bbsentials.forge.CommandImplementations.*;
import net.minecraftforge.client.ClientCommandHandler;

public class Commands implements MCCommand {
    public static <T extends AbstractPacket> void sendPacket(T packet) {
        BBsentials.connection.sendPacket(packet);
    }

    public void registerMain() {
        ClientCommandHandler.instance.registerCommand(new CommandBBI());
        ClientCommandHandler.instance.registerCommand(new CommandGoblinRaid());
        ClientCommandHandler.instance.registerCommand(new Command2xPowder());
        ClientCommandHandler.instance.registerCommand(new CommandBetterTogether());
        ClientCommandHandler.instance.registerCommand(new CommandRaffle());
        ClientCommandHandler.instance.registerCommand(new CommandGoneWithTheWind());
        ClientCommandHandler.instance.registerCommand(new CommandChChest());
        ClientCommandHandler.instance.registerCommand(new CommandBC());
        ClientCommandHandler.instance.registerCommand(new CommandBingoChat());
        ClientCommandHandler.instance.registerCommand(new CommandOpenConfig());
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
        if (hasMod) {
            ClientCommandHandler.instance.registerCommand(new CommandBAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandBMute());
            ClientCommandHandler.instance.registerCommand(new CommandBBan());
        }
        if (hasSplasher) {
            ClientCommandHandler.instance.registerCommand(new CommandSplashAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandGetLeechers());
        }
    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        sendPacket(new SplashNotifyPacket(0, hubNumber, BBsentials.config.getUsername(), locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste));
    }
}
