package de.hype.bbsentials.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.APIUtils;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.MCCommand;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.ChChestItems;
import de.hype.bbsentials.shared.constants.MiningEvents;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.*;
import de.hype.bbsentials.shared.packets.function.SplashNotifyPacket;
import de.hype.bbsentials.shared.packets.mining.ChChestPacket;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import de.hype.bbsentials.shared.packets.network.BingoChatMessagePacket;
import de.hype.bbsentials.shared.packets.network.BroadcastMessagePacket;
import de.hype.bbsentials.shared.packets.network.InternalCommandPacket;
import de.hype.bbsentials.shared.packets.network.PunishUserPacket;
import dev.xpple.clientarguments.arguments.CBlockPosArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Commands implements MCCommand {
    Event<ClientCommandRegistrationCallback> event = ClientCommandRegistrationCallback.EVENT;

    private static void simpleCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, String[] parameters) {
        dispatcher.register(
                literal(commandName)
                        .executes((context) -> {
                            sendPacket(new InternalCommandPacket(commandName, parameters));
                            return 1;
                        })
        );
    }

    private static void miningEvent(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, MiningEvents event) {
        dispatcher.register(
                literal(commandName)
                        .executes((context) -> {
                            try {
                                sendPacket(new MiningEventPacket(event,
                                        BBsentials.generalConfig.getUsername(), Objects.requireNonNull(EnvironmentCore.utils.getCurrentIsland())));
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError(e.getMessage());
                            }
                            return 1;
                        })
        );
    }

    public static <T extends AbstractPacket> void sendPacket(T packet) {
        BBsentials.connection.sendPacket(packet);
    }

    public void registerMain() {
        event.register((dispatcher, registryAccess) -> dispatcher.register(literal("creport")
                .then(argument("Player_Name", StringArgumentType.string())
                        .executes((context) -> {
                            String playerName = StringArgumentType.getString(context, "Player_Name");
                            BBsentials.sender.addSendTask("/creport " + playerName, 0);
                            BBsentials.temporaryConfig.alreadyReported.add(playerName);
                            return 1;
                        }))));//creport helper → no double report during same launch
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "goblinraid", MiningEvents.GOBLIN_RAID));/*goblinraid*/
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "2xpowder", MiningEvents.DOUBLE_POWDER));/*2xpowder*/
        event.register((dispatcher, registryAccess) -> dispatcher.register(literal("getLobbyTime")
                .executes((context) -> {
                    Chat.sendPrivateMessageToSelfSuccess("Day: " + EnvironmentCore.utils.getLobbyDay());
                    return 1;
                })));/*2xpowder*/
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "bettertogether", MiningEvents.BETTER_TOGETHER));/*b2g*/
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "raffle", MiningEvents.RAFFLE));/*raffle*/
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "gonewiththewind", MiningEvents.GONE_WITH_THE_WIND));/*gonewiththewind*/
        event.register((dispatcher, registryAccess) -> miningEvent(dispatcher, "mithrilgourmand", MiningEvents.MITHRIL_GOURMAND));/*gonewiththewind*/
        event.register((dispatcher, registryAccess) -> dispatcher.register(literal("chchest")
                .then(argument("Item", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            List<String> items = ChChestItems.getAllItems().stream().map(ChChestItem::getDisplayName).toList();
                            String inputTemporary = builder.getRemaining().replace("\"", "");
                            int lastIndex = inputTemporary.lastIndexOf(";");
                            boolean suggestLastQuote;
                            if (lastIndex == -1) {
                                suggestLastQuote = true;
                                lastIndex = 0;
                            }
                            else {
                                lastIndex++;
                                suggestLastQuote = false;
                            }
                            String input = inputTemporary.substring(0, lastIndex);
                            String currentItemSuggestionStart = input.substring(lastIndex);

                            List<String> suggestions = items.stream().filter((item) -> item.toLowerCase().startsWith(currentItemSuggestionStart.toLowerCase())).map((newItem) -> {
                                if (suggestLastQuote) {
                                    return "\"" + input + newItem + "\"";
                                }
                                else {
                                    return "\"" + input + newItem;
                                }
                            }).toList();

                            return CommandSource.suggestMatching(suggestions, builder);
                        })
                        .then(argument("coordinates", CBlockPosArgumentType.blockPos())
                                .then(argument("ContactWay", StringArgumentType.string())
                                        .suggests(((context, builder) -> CommandSource.suggestMatching(new String[]{"\"/msg " + BBsentials.generalConfig.getUsername() + " bb:party me\"", "\"/p join " + BBsentials.generalConfig.getUsername() + "\""}, builder)))
                                        .then(argument("extraMessage", StringArgumentType.greedyString())
                                                .executes((context) -> {
                                                            String item = StringArgumentType.getString(context, "Item");
                                                            BlockPos pos = CBlockPosArgumentType.getCBlockPos(context, "coordinates");
                                                            String contactWay = StringArgumentType.getString(context, "ContactWay");
                                                            String extraMessage = StringArgumentType.getString(context, "extraMessage");

                                                            if (EnvironmentCore.utils.getLobbyTime() >= 408000) {
                                                                context.getSource().sendError(Text.of("§cThis lobby is already closed and no one can be warped in!"));
                                                                return 1;
                                                            }
                                                            if (!BBsentials.partyConfig.allowServerPartyInvite) {
                                                                Chat.sendPrivateMessageToSelfImportantInfo("Enabled Server Party invites temporarily. Will be disabled on Server swap");
                                                                BBsentials.partyConfig.allowServerPartyInvite = true;
                                                                ServerSwitchTask.onServerLeaveTask(() -> BBsentials.partyConfig.allowServerPartyInvite = false);
                                                            }
                                                            if (!BBsentials.partyConfig.allowBBinviteMe && contactWay.contains("bb:party me")) {
                                                                Chat.sendPrivateMessageToSelfImportantInfo("Enabled bb:party invites temporarily. Will be disabled on Server swap");
                                                                BBsentials.partyConfig.allowBBinviteMe = true;
                                                                ServerSwitchTask.onServerLeaveTask(() -> BBsentials.partyConfig.allowBBinviteMe = false);
                                                            }
                                                            sendPacket(new ChChestPacket(new ChestLobbyData(List.of(new ChChestData("", new Position(pos.getX(), pos.getY(), pos.getZ()), ChChestItems.getItem(item.split(";")))), EnvironmentCore.utils.getServerId(), contactWay, extraMessage, StatusConstants.OPEN)));
                                                            return 1;
                                                        }
                                                )
                                        )
                                        .executes((context) -> {
                                                    String item = StringArgumentType.getString(context, "Item");
                                                    BlockPos pos = CBlockPosArgumentType.getCBlockPos(context, "coordinates");
                                                    String contactWay = StringArgumentType.getString(context, "ContactWay");
                                                    if (EnvironmentCore.utils.getLobbyTime() >= 408000) {
                                                        context.getSource().sendError(Text.of("§cThis lobby is already closed and no one can be warped in!"));
                                                        return 1;
                                                    }
                                                    sendPacket(new ChChestPacket(new ChestLobbyData(List.of(new ChChestData("", new Position(pos.getX(), pos.getY(), pos.getZ()), ChChestItems.getItem(item.split(";")))), EnvironmentCore.utils.getServerId(), contactWay, "", StatusConstants.OPEN)));
                                                    return 1;
                                                }
                                        )
                                )
                        )
                )
        ));/*chchest*/
        event.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("bc")
                        .then(argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                .executes((context) -> {
                                    String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                    sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                    return 1;
                                })
                        )
        ));/*BincoChatShort*/
        event.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("bingochat")
                        .then(argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                .executes((context) -> {
                                    String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                    sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                    return 1;
                                })
                        )
        ));/*BingoChatLong*/
        event.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("bbi").then(literal("discord")
                        .then(literal("refreshConnection").executes(context -> {
                            if (BBsentials.dcGameSDK != null) {
                                Chat.sendPrivateMessageToSelfInfo("The refresh may take a couple of seconds");
                                BBsentials.executionService.execute(() -> BBsentials.dcGameSDK.connectToDiscord());
                                return 1;
                            }
                            else {
                                Chat.sendPrivateMessageToSelfError("You cant refresh something which does not exist.");
                                return 0;
                            }
                            // Due too how stuff works you cant initialise the stuff here,
                            // because it would freeze the screen.
                            // And if the sdk is downloaded, this is basically a crash
                        }))
                        .then(literal("openVCSettings").executes(context -> {
                            try {
                                BBsentials.dcGameSDK.openVoiceSettings();
                                Chat.sendPrivateMessageToSelfSuccess("Success");
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Error");
                                return 2;
                            }
                            return 1;
                        }))
                        .then(literal("join")
                                .then(argument("lobbyid", LongArgumentType.longArg())
                                        .then(argument("secret", StringArgumentType.string()).executes(context -> {
                                            try {
                                                BBsentials.dcGameSDK.blockingJoinLobby(LongArgumentType.getLong(context, "lobbyid"), StringArgumentType.getString(context, "secret"));
                                                Chat.sendPrivateMessageToSelfSuccess("Success");
                                            } catch (Exception e) {
                                                Chat.sendPrivateMessageToSelfError("Error");
                                                return 2;
                                            }
                                            return 1;
                                        })))
                                .then(argument("activity secret", StringArgumentType.string())
                                        .executes(context -> {
                                            try {
                                                BBsentials.dcGameSDK.blockingJoinLobbyWithActivitySecret(StringArgumentType.getString(context, "activity secret"));
                                                Chat.sendPrivateMessageToSelfSuccess("Success");
                                            } catch (Exception e) {
                                                Chat.sendPrivateMessageToSelfError("Error");
                                                return 2;
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("mute").executes(context -> {
                            try {
                                boolean mute = !BBsentials.dcGameSDK.getCore().voiceManager().isSelfMute();
                                BBsentials.dcGameSDK.getCore().voiceManager().setSelfMute(mute);
                                if (mute) Chat.sendPrivateMessageToSelfSuccess("You are now muted");
                                else Chat.sendPrivateMessageToSelfSuccess("You are now UNmuted");
                                return 1;
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Could not mute etc. Are you connected?");
                                return 0;
                            }
                        }))
                        .then(literal("deafen").executes(context -> {
                            try {
                                boolean mute = !BBsentials.dcGameSDK.getCore().voiceManager().isSelfMute();
                                BBsentials.dcGameSDK.getCore().voiceManager().setSelfMute(mute);
                                if (mute) Chat.sendPrivateMessageToSelfSuccess("You are now deafened");
                                else Chat.sendPrivateMessageToSelfSuccess("You are not longer deafened");
                                return 1;
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Could not mute etc. Are you connected?");
                                return 0;
                            }
                        }))
                        .then(literal("getActivitySecret").executes(context -> {
                            if (BBsentials.dcGameSDK.getCurrentLobby() == null) {
                                Chat.sendPrivateMessageToSelfError("You are not in a Lobby.");
                                return 0;
                            }
                            Chat.sendPrivateMessageToSelfText(Message.tellraw("{\"text\":\"click here to copy the secret into your clipboard.\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"@actvitysecret\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"This can be used with /bbi discord join {secret} to connect to the lobby.\"]}}".replace("@actvitysecret", BBsentials.dcGameSDK.getLobbyManager().getLobbyActivitySecret(BBsentials.dcGameSDK.getCurrentLobby()))));
                            return 1;
                        }))
                        .then(literal("createDefault").executes(context -> {
                            BBsentials.dcGameSDK.blockingCreateDefaultLobby();
                            return 1;
                        }))
                        .then(literal("joinVC").executes(context -> {
                            try {
                                BBsentials.dcGameSDK.joinVC();
                                Chat.sendPrivateMessageToSelfSuccess("Success");
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Error");
                                return 2;
                            }
                            return 1;
                        }))
                        .then(literal("disconnect").executes(context -> {
                            try {
                                BBsentials.dcGameSDK.disconnectLobby();
                                Chat.sendPrivateMessageToSelfSuccess("Success");
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Error");
                                return 2;
                            }
                            return 1;
                        }))
                        .then(literal("disconnectVC").executes(context -> {
                            try {
                                BBsentials.dcGameSDK.disconnectLobbyVC();
                                Chat.sendPrivateMessageToSelfSuccess("Success");
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError("Error");
                                return 2;
                            }
                            return 1;
                        }))
                )));/*BingoChatLong*/
//        event.register((dispatcher, registryAccess) -> {
//            dispatcher.register(ClientCommandManager.literal("p")
//                    .then(ClientCommandManager.argument("subcommand", StringArgumentType.word())
//                            .suggests((context, builder) -> {
//                                // Your custom suggestions logic
//                                String input = builder.getRemaining().toLowerCase();
//
//                                if (input.startsWith("w")) {
//                                    builder.suggest("warp");
//                                }
//
//                                if (input.startsWith("d")) {
//                                    builder.suggest("disband");
//                                }
//
//                                return builder.buildFuture();
//                            })
//                            .executes(context -> {
//                                String subcommand = StringArgumentType.getString(context, "subcommand");
//
//                                switch (subcommand) {
//                                    case "warp":
//                                        context.getSource().sendFeedback(Text.of("Teleporting to warp..."));
//                                        break;
//                                    case "disband":
//                                        context.getSource().sendFeedback(Text.of("Disbanding the group..."));
//                                        break;
//                                    default:
//                                        context.getSource().sendError(Text.of("Unknown subcommand: " + subcommand));
//                                        break;
//                                }
//
//                                return 1;
//                            })));
//        });
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
        if (hasMod) {
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("bannounce")
                            .then(argument("message", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "message");
                                        sendPacket(new BroadcastMessagePacket("", "", message));
                                        return 1;
                                    })
                            )
            ));/*bAnnounce*/
            event.register((dispatcher, registryAccess) -> dispatcher.register(literal("punish")
                            .then(literal("ban")
                                    .then(argument("userId/mcusername", StringArgumentType.string())
                                            .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
                                                    .then(argument("reason", StringArgumentType.greedyString())
                                                            .executes((context) -> {
                                                                String identification = StringArgumentType.getString(context, "userId/mcusername");
                                                                String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
                                                                String reason = StringArgumentType.getString(context, "reason");
                                                                int userId = -1;
                                                                String mcusername = "";
                                                                try {
                                                                    userId = Integer.parseInt(identification);
                                                                } catch (Exception e) {
                                                                    mcusername = identification;
                                                                }

                                                                Date till;
                                                                try {
                                                                    till = PunishmentData.getTillDateFromDurationString(duration);
                                                                } catch (Exception e) {
                                                                    Chat.sendPrivateMessageToSelfError(e.getMessage());
                                                                    return 0;
                                                                }
                                                                sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.BAN, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                            )
                            .then(literal("mute")
                                    .then(argument("userId/mcusername", StringArgumentType.string())
                                            .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
                                                    .then(argument("reason", StringArgumentType.greedyString())
                                                            .executes((context) -> {
                                                                String identification = StringArgumentType.getString(context, "userId/mcusername");
                                                                String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
                                                                String reason = StringArgumentType.getString(context, "reason");
                                                                int userId = -1;
                                                                String mcusername = "";
                                                                try {
                                                                    userId = Integer.parseInt(identification);
                                                                } catch (Exception e) {
                                                                    mcusername = identification;
                                                                }

                                                                Date till;
                                                                try {
                                                                    till = PunishmentData.getTillDateFromDurationString(duration);
                                                                } catch (Exception e) {
                                                                    Chat.sendPrivateMessageToSelfError(e.getMessage());
                                                                    return 0;
                                                                }
                                                                sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.MUTE, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                            )
                            .then(literal("blacklist")
                                    .then(argument("userId/mcusername", StringArgumentType.string())
                                            .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
                                                    .then(argument("reason", StringArgumentType.greedyString())
                                                            .executes((context) -> {
                                                                String identification = StringArgumentType.getString(context, "userId/mcusername");
                                                                String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
                                                                String reason = StringArgumentType.getString(context, "reason");
                                                                int userId = -1;
                                                                String mcusername = "";
                                                                try {
                                                                    userId = Integer.parseInt(identification);
                                                                } catch (Exception e) {
                                                                    mcusername = identification;
                                                                }

                                                                Date till;
                                                                try {
                                                                    till = PunishmentData.getTillDateFromDurationString(duration);
                                                                } catch (Exception e) {
                                                                    Chat.sendPrivateMessageToSelfError(e.getMessage());
                                                                    return 0;
                                                                }
                                                                sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.BLACKLIST, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                            )
                    )
            );/*bpunish*/

            event.register((dispatcher, registryAccess) -> dispatcher.register(literal("bgetinfo")
                    .then(argument("userId/mcusername", StringArgumentType.string())
                            .executes((context) -> {
                                String identification = StringArgumentType.getString(context, "userId/mcusername");
                                sendPacket(new InternalCommandPacket(InternalCommandPacket.GET_USER_INFO, new String[]{identification}));
                                return 1;
                            })
                    )
            ));/*getInfo*/
        }
        if (hasSplasher) {
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("splashAnnounce")
                            .then(argument("Hub", IntegerArgumentType.integer(1, 28))
                                    .then(argument("location", StringArgumentType.string())
                                            .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"kat", "bea", "guild-house"}, builder))
                                            .then(argument("lasswaste", StringArgumentType.string())
                                                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"true", "false"}, builder))
                                                    .then(argument("extramessage", StringArgumentType.greedyString())
                                                            .executes((context) -> {
                                                                int hub = IntegerArgumentType.getInteger(context, "Hub");
                                                                String extramessage = StringArgumentType.getString(context, "extramessage");
                                                                String location = StringArgumentType.getString(context, "location");
                                                                boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
                                                                splashAnnounce(hub, location, extramessage, lessWaste);
                                                                return 1;
                                                            })
                                                    )
                                                    .executes((context) -> {
                                                        int hub = IntegerArgumentType.getInteger(context, "Hub");
                                                        String location = StringArgumentType.getString(context, "location");
                                                        boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
                                                        splashAnnounce(hub, location, "", lessWaste);
                                                        return 1;
                                                    })
                                            ))
                                    .executes((context) -> {
                                        int hub = IntegerArgumentType.getInteger(context, "Hub");
                                        String location = "bea";
                                        splashAnnounce(hub, location, "", true);
                                        return 1;
                                    })

                            )
            ));/*SplashAnnounce*/
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("requestpottimes")
                            .executes((context) -> {
                                sendPacket(new InternalCommandPacket(InternalCommandPacket.REQUEST_POT_DURATION, new String[0]));
                                return 1;
                            })
            ));/*requestpottimes*/
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("getLeecher")
                            .executes((context) -> {
                                BBsentials.executionService.execute(() -> {
                                    UpdateListenerManager.splashStatusUpdateListener.showOverlay = true;
                                    Chat.sendPrivateMessageToSelfInfo("Leeching Players: " + String.join(", ", EnvironmentCore.utils.getSplashLeechingPlayers()));
                                    BBsentials.executionService.schedule(() -> UpdateListenerManager.splashStatusUpdateListener.showOverlay = false,
                                            2, TimeUnit.MINUTES);
                                });
                                return 1;
                            })
            ));/*getLeecher*/
        }
        if (hasAdmin) {
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("bshutdown")
                            .then(argument("Reason", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"Emergency Shutdown", "System Shutdown", "Other"}, builder))
                                    .executes((context) -> {
                                        String reason = StringArgumentType.getString(context, "Reason");
                                        sendPacket(new InternalCommandPacket(InternalCommandPacket.SHUTDOWN_SERVER, new String[]{reason}));
                                        return 1;
                                    })
                            )
            ));/*BBShutdown*/
            event.register((dispatcher, registryAccess) -> dispatcher.register(
                    literal("bsetmotd")
                            .then(argument("Message", StringArgumentType.greedyString())
                                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{""}, builder))
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message").trim();
                                        sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_MOTD, new String[]{message}));
                                        return 1;
                                    })
                            )
            ));/*BBServerMotd*/
        }
    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        try {
            sendPacket(new SplashNotifyPacket(new SplashData(BBsentials.generalConfig.getUsername(), hubNumber, locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste)));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
}
