package de.hype.bbsentials.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static de.hype.bbsentials.forge.client.BBsentials.connectToBBserver;
import static de.hype.bbsentials.forge.client.BBsentials.getConfig;


public class CommandBBI extends CommandBase {

    @Override
    public String getCommandName() {
        return "bbi";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bbi <reconnect|config>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi <reconnect|config|set-key>"));
            return;
        }

        String subCommand = args[0];
        switch (subCommand) {
            case "reconnect":
                connectToBBserver();
                break;

            case "config":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bbi config <category>"));
                    return;
                }

                String category = args[1];
                switch (category) {
                    case "save":
                        getConfig().save();
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Saved config successfully"));
                        break;

                    case "load":
                        BBsentials.config = Config.load();
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
