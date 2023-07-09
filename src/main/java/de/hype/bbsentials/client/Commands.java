package de.hype.bbsentials.client;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.hype.bbsentials.api.Discord;
import de.hype.bbsentials.chat.Chat;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static de.hype.bbsentials.chat.Chat.*;
import static de.hype.bbsentials.client.BBsentials.getConfig;

public class Commands {
    Commands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("hci").then(ClientCommandManager.literal("menu").then(ClientCommandManager.argument("category", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for menu subfolder
                        return CommandSource.suggestMatching(new String[]{"sbacm", "acm", "pcm"}, builder);
                    }).then(ClientCommandManager.argument("message", StringArgumentType.greedyString()).executes((context) -> {
                        String category = StringArgumentType.getString(context, "category");
                        String message = StringArgumentType.getString(context, "message");
                        followMenu(category, message);
                        return 1;
                    }))).executes((context) -> {
                        sendPrivateMessageToSelf(Formatting.RED + "Invalid");
                        return 1;
                    })).then(ClientCommandManager.literal("config").then(ClientCommandManager.argument("category", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for config subfolder
                        return CommandSource.suggestMatching(new String[]{"save", "reset", "load"}, builder);
                    }).executes((context) -> {
                        String category = StringArgumentType.getString(context, "category");
                        if (category.equals("save")) {
                            getConfig().save();
                            sendPrivateMessageToSelf(Formatting.GREEN + "Saved config successfully");
                        }
                        else if (category.equals("load")) {
                            BBsentials.config = Config.load();
                        }
                        else if (category.equals("reset")) {
                            // Reset logic here
                        }
                        return 1;
                    })).then(ClientCommandManager.literal("set-value").then(ClientCommandManager.argument("className", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for classes
                        ArrayList<String> classNames = new ArrayList<>();
                        classNames.add("Config");
                        // Replace with your own logic to retrieve class names
                        return CommandSource.suggestMatching(classNames, builder);
                    }).then(ClientCommandManager.argument("variableName", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for variable names
                        List<String> variableNames = null; // Replace with your own logic to retrieve variable names
                        variableNames = List.of(getVariableInfo("de.hype.bbsentials.client", "Config"));
                        return CommandSource.suggestMatching(variableNames, builder);
                    }).then(ClientCommandManager.argument("variableValue", StringArgumentType.string()).executes((context) -> {
                        // Handle "variableName" and "variableValue" logic here
                        String variableName = StringArgumentType.getString(context, "variableName");
                        String variableValue = StringArgumentType.getString(context, "variableValue");
                        try {
                            setVariableValue(getConfig(), variableName, variableValue);
                            getConfig().save();
                        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                                 InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                        }
                        return 1;
                    }))))).then(ClientCommandManager.literal("get-value").then(ClientCommandManager.argument("className", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for classes
                        ArrayList<String> classNames = new ArrayList<>();
                        classNames.add("Config");
                        // Replace with your own logic to retrieve class names
                        return CommandSource.suggestMatching(classNames, builder);
                    }).then(ClientCommandManager.argument("variableName", StringArgumentType.string()).suggests((context, builder) -> {
                        // Provide tab-completion options for variable names
                        List<String> variableNames = null; // Replace with your own logic to retrieve variable names
                        variableNames = List.of(getVariableInfo("de.hype.bbsentials.client", "Config"));
                        return CommandSource.suggestMatching(variableNames, builder);
                    }).executes((context) -> {
                        // Handle "variableName" and "variableValue" logic here
                        String variableName = StringArgumentType.getString(context, "variableName");
                        try {
                            Chat.getVariableValue(getConfig(), variableName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 1;
                    }))).executes((context) -> {
                        // Handle the case when "config" argument is not provided
                        // ...
                        return 1;
                    })))

            );

        }); //hci
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("warp").then(ClientCommandManager.argument("destination", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(new String[]{"desert", "hub", "dhub", "nether", "isle", "wizard", "portal", "mines", "forge", "ch", "crystals", "nucleus", "end", "drag", "void", "castle", "howl", "park", "jungle", "nest", "arachne", "spider", "deep", "barn", "home", "kuurda", "wasteland", "dragontail", "scarleton", "smold", "garden", "da", "crypt", "museum", "trapper", "dungeon_hub"}, builder);
            }).executes((context) -> {
                // Handle "variableName" and "variableValue" logic here
                String destination = StringArgumentType.getString(context, "destination");
                getConfig().sender.addSendTask("/warp " + destination, 0);
                return 1;
            })));
        }); //warp test
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("creport").then(ClientCommandManager.argument("Player_Name", StringArgumentType.string()).executes((context) -> {
                String playerName = StringArgumentType.getString(context, "Player_Name");
                getConfig().sender.addSendTask("/creport " + playerName, 0);
                getConfig().addReported(playerName);
                return 1;
            })));
        });//creport helper → no double report during same launch
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("hp").then(ClientCommandManager.literal("accept").then(ClientCommandManager.argument("player", StringArgumentType.string()).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party accept " + player);
                return 1;
            }))).then(ClientCommandManager.literal("chat").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party chat");
                return 1;
            })).then(ClientCommandManager.literal("demote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party demote " + player);
                return 1;
            }))).then(ClientCommandManager.literal("disband").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party disband");
                return 1;
            })).then(ClientCommandManager.literal("kick").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party kick " + player);
                return 1;
            }))).then(ClientCommandManager.literal("kickoffline").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party kickoffline");
                return 1;
            })).then(ClientCommandManager.literal("leave").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party leave");
                return 1;
            })).then(ClientCommandManager.literal("list").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party list");
                return 1;
            })).then(ClientCommandManager.literal("mute").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party mute");
                return 1;
            })).then(ClientCommandManager.literal("poll").then(ClientCommandManager.argument("question/answer/answer/answer", StringArgumentType.greedyString()).executes((context) -> {
                String questionAndAnswers = StringArgumentType.getString(context, "question answer answer (answer)");
                getConfig().sender.addImmediateSendTask("/party poll " + questionAndAnswers);
                return 1;
            }))).then(ClientCommandManager.literal("private").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party private");
                return 1;
            })).then(ClientCommandManager.literal("promote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party promote " + player);
                return 1;
            }))).then(ClientCommandManager.literal("setting").then(ClientCommandManager.literal("allinvite")).executes((context) -> {
                        String setting = StringArgumentType.getString(context, "setting");
                        getConfig().sender.addImmediateSendTask("/party setting " + setting);
                        return 1;
                    })

            ).then(ClientCommandManager.literal("transfer").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party transfer " + player);
                return 1;
            }))).then(ClientCommandManager.literal("warp").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party warp");
                return 1;
            })).executes(context -> {
                getConfig().sender.addImmediateSendTask("/p");
                return 1;
            }));
        }); //party test
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("goblinraid")
                            .executes((context) -> {
                                Discord.sendWebhookMessage("?goblinraid");
                                return 1;
                            })
            );
        });/*goblinraid*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("2xpowder")
                            .executes((context) -> {
                                Discord.sendWebhookMessage("?2xpowder");
                                return 1;
                            })
            );
        });/*2xpowder*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bettertogether")
                            .executes((context) -> {
                                Discord.sendWebhookMessage("?bettertogether");
                                return 1;
                            })
            );
        });/*b2g*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("raffle")
                            .executes((context) -> {
                                Discord.sendWebhookMessage("?raffle");
                                return 1;
                            })
            );
        });/*raffle*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("gonewiththewind")
                            .executes((context) -> {
                                Discord.sendWebhookMessage("?gonewiththewind");
                                return 1;
                            })
            );
        });/*gonewiththewind*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("chchest")
                    .then(ClientCommandManager.argument("Item", StringArgumentType.string())
                            .suggests((context, builder) -> {
                                return CommandSource.suggestMatching(new String[]{"PrehistoricEgg", "Pickonimbus2000", "ElectronTransmitter", "FTX3070", "RobotronReflector", "ControlSwitch", "SyntheticHeart", "SuperliteMotor", "BlueGoblinEgg", "YellowGoblinEgg", "FlawlessAmberGemstone", "FlawlessJadeGemstone", "FlawlessSapphireGemstone", "FlawlessRubyGemstone", "FlawlessAmethystGemstone", "JungleHeart", "FlawlessTopazGemstone", "FlawlessJasperGemstone"}, builder);
                            })
                            .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                                    .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                                            .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                                                    .then(ClientCommandManager.argument("ContactWay", StringArgumentType.greedyString())
                                                            .suggests(((context, builder) -> {
                                                                return CommandSource.suggestMatching(new String[]{"Boop me", "Put IGN's in Thread", "bb:party me", "/p join"}, builder);
                                                            }))
                                                            .executes((context) -> {
                                                                        String destination = StringArgumentType.getString(context, "Item");
                                                                        int x = IntegerArgumentType.getInteger(context, "X");
                                                                        int y = IntegerArgumentType.getInteger(context, "Y");
                                                                        int z = IntegerArgumentType.getInteger(context, "Z");
                                                                        String contactWay = StringArgumentType.getString(context, "ContactWay");

                                                                        String combinedString = "?chchest " + destination + " " + x + " " + y + " " + z + " " + contactWay;
                                                                        Discord.sendWebhookMessage(combinedString); // Call your method with the combined string
                                                                        return 1;
                                                                    }
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
        });
    }
}