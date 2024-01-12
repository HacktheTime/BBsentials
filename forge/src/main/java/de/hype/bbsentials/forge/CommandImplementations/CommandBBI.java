package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bbsentials.client.common.client.BBsentials.connectToBBserver;



public class CommandBBI extends CommandBase {

    @Override
    public String getCommandName() {
        return "bbi";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bbi <reconnect|configManager>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi <reconnect|configManager|set-key>"));
            return;
        }

        String subCommand = args[0];
        switch (subCommand) {
            case "reconnect":
                connectToBBserver();
                break;

            case "configManager":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi configManager <category>"));
                    return;
                }

                String category = args[1];
                switch (category) {
                    case "save":
                        ConfigManager.saveAll();
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Saved configManager successfully"));
                        break;

                    case "load":
                        ConfigManager.loadConfigs();
                        break;

                    default:
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown category: " + category));
                        break;
                }
                break;

            case "set-key":
                if (args.length < 4) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi set-key <key> set-server <value>"));
                    return;
                }

                String key = args[1];
                if (args[2].equalsIgnoreCase("set-server")) {
                    String value = args[3];
                    // Handle setting the key and value logic here for server
                }
                else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi set-key <key> set-server <value>"));
                }
                break;

            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown command: " + subCommand));
                break;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
