package de.hype.bbsentials.fabric.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.APIUtils;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.MCCommand;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.ChChestItems;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.MiningEvents;
import de.hype.bbsentials.shared.objects.*;
import de.hype.bbsentials.shared.packets.function.SplashNotifyPacket;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import de.hype.bbsentials.shared.packets.network.BingoChatMessagePacket;
import de.hype.bbsentials.shared.packets.network.BroadcastMessagePacket;
import de.hype.bbsentials.shared.packets.network.InternalCommandPacket;
import de.hype.bbsentials.shared.packets.network.PunishUserPacket;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static de.hype.bbsentials.fabric.command.ClientCommandManager.argument;
import static de.hype.bbsentials.fabric.command.ClientCommandManager.literal;

public class Commands implements MCCommand {

    private static void simpleCommand(BBCommandDispatcher dispatcher, String commandName, String[] parameters) {
        dispatcher.register(
                literal(commandName)
                        .executes((context) -> {
                            sendPacket(new InternalCommandPacket(commandName, parameters));
                            return 1;
                        })
        );
    }

    private static void miningEvent(BBCommandDispatcher dispatcher, String commandName, MiningEvents event) {
        dispatcher.register(
                literal(commandName).requires(commandSource -> {
                            return commandSource.BBsentials$getCurrentIsland() == Islands.DWARVEN_MINES || (commandSource.BBsentials$getCurrentIsland() == Islands.CRYSTAL_HOLLOWS && !event.isDWEventOnly());
                        })
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
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher) -> {
            miningEvent(dispatcher, "goblinraid", MiningEvents.GOBLIN_RAID);/*goblinraid*/
            miningEvent(dispatcher, "2xpowder", MiningEvents.DOUBLE_POWDER);/*2xpowder*/
            miningEvent(dispatcher, "bettertogether", MiningEvents.BETTER_TOGETHER);/*b2g*/
            miningEvent(dispatcher, "raffle", MiningEvents.RAFFLE);/*raffle*/
            miningEvent(dispatcher, "gonewiththewind", MiningEvents.GONE_WITH_THE_WIND);/*gonewiththewind*/
            miningEvent(dispatcher, "mithrilgourmand", MiningEvents.MITHRIL_GOURMAND);/*gonewiththewind*/
            dispatcher.register(literal("chchest")
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
                            .then(argument("coordinates", CBlockPosArgument.blockPos())
                                    .then(argument("ContactWay", StringArgumentType.string())
                                            .suggests(((context, builder) -> CommandSource.suggestMatching(new String[]{"\"/msg " + BBsentials.generalConfig.getUsername() + " bb:party me\"", "\"/p join " + BBsentials.generalConfig.getUsername() + "\""}, builder)))
                                            .then(argument("extraMessage", StringArgumentType.greedyString())
                                                    .requires(fabricClientCommandSource -> {
                                                        return EnvironmentCore.utils.getCurrentIsland() == Islands.CRYSTAL_HOLLOWS && BBsentials.generalConfig.hasBBRoles(BBRole.CHCHEST_ANNOUNCE_PERM);
                                                    })
                                                    .executes((context) -> {
                                                                String item = StringArgumentType.getString(context, "Item");
                                                                BlockPos pos = CBlockPosArgument.getBlockPos((CommandContext<FabricClientCommandSource>) (Object) context, "coordinates");
                                                                String contactWay = StringArgumentType.getString(context, "ContactWay");
                                                                String extraMessage = StringArgumentType.getString(context, "extraMessage");
                                                        BBsentials.connection.annonceChChest(new Position(pos.getX(), pos.getY(), pos.getZ()), ChChestItems.getItems(item.split(";")), contactWay, extraMessage);
                                                                return 1;
                                                            }
                                                    )
                                            )
                                            .executes((context) -> {
                                                        String item = StringArgumentType.getString(context, "Item");
                                                        BlockPos pos = CBlockPosArgument.getBlockPos((CommandContext<FabricClientCommandSource>) (Object) context, "coordinates");
                                                        String contactWay = StringArgumentType.getString(context, "ContactWay");
                                                BBsentials.connection.annonceChChest(new Position(pos.getX(), pos.getY(), pos.getZ()), ChChestItems.getItems(item.split(";")), contactWay, "");
                                                        return 1;
                                                    }
                                            )
                                    )
                            )
                    )
            );/*chchest*/
            dispatcher.register(
                    literal("bc").requires((fabricClientCommandSource) ->
                                    BBsentials.connection.isConnected()
                            )
                            .then(argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                        return 1;
                                    })
                            )
            );/*BincoChatShort*/
            dispatcher.register(
                    literal("bingochat").requires((fabricClientCommandSource) -> BBsentials.connection.isConnected())
                            .then(argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                        return 1;
                                    })
                            )
            );/*BingoChatLong*/
            dispatcher.register(
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
                    ));/*BingoChatLong*/
        }));
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher,
                                     boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher) -> {
            if (hasMod) {
                dispatcher.register(
                        literal("bannounce")
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes((context) -> {
                                            String message = StringArgumentType.getString(context, "message");
                                            sendPacket(new BroadcastMessagePacket("", "", message));
                                            return 1;
                                        })
                                )
                );/*bAnnounce*/
//                dispatcher.register(literal("punish")
//                        .then(literal("ban")
//                                .then(argument("userId/mcusername", StringArgumentType.string())
//                                        .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
//                                                .then(argument("reason", StringArgumentType.greedyString())
//                                                        .executes((context) -> {
//                                                            String identification = StringArgumentType.getString(context, "userId/mcusername");
//                                                            String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
//                                                            String reason = StringArgumentType.getString(context, "reason");
//                                                            int userId = -1;
//                                                            String mcusername = "";
//                                                            try {
//                                                                userId = Integer.parseInt(identification);
//                                                            } catch (Exception e) {
//                                                                mcusername = identification;
//                                                            }
//
//                                                            Date till;
//                                                            try {
//                                                                till = PunishmentData.getTillDateFromDurationString(duration);
//                                                            } catch (Exception e) {
//                                                                Chat.sendPrivateMessageToSelfError(e.getMessage());
//                                                                return 0;
//                                                            }
//                                                            sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.BAN, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
//                                                            return 1;
//                                                        })
//                                                )
//                                        )
//                                )
//                        )
//                        .then(literal("mute")
//                                .then(argument("userId/mcusername", StringArgumentType.string())
//                                        .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
//                                                .then(argument("reason", StringArgumentType.greedyString())
//                                                        .executes((context) -> {
//                                                            String identification = StringArgumentType.getString(context, "userId/mcusername");
//                                                            String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
//                                                            String reason = StringArgumentType.getString(context, "reason");
//                                                            int userId = -1;
//                                                            String mcusername = "";
//                                                            try {
//                                                                userId = Integer.parseInt(identification);
//                                                            } catch (Exception e) {
//                                                                mcusername = identification;
//                                                            }
//
//                                                            Date till;
//                                                            try {
//                                                                till = PunishmentData.getTillDateFromDurationString(duration);
//                                                            } catch (Exception e) {
//                                                                Chat.sendPrivateMessageToSelfError(e.getMessage());
//                                                                return 0;
//                                                            }
//                                                            sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.MUTE, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
//                                                            return 1;
//                                                        })
//                                                )
//                                        )
//                                )
//                        )
//                        .then(literal("blacklist")
//                                .then(argument("userId/mcusername", StringArgumentType.string())
//                                        .then(argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
//                                                .then(argument("reason", StringArgumentType.greedyString())
//                                                        .executes((context) -> {
//                                                            String identification = StringArgumentType.getString(context, "userId/mcusername");
//                                                            String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
//                                                            String reason = StringArgumentType.getString(context, "reason");
//                                                            int userId = -1;
//                                                            String mcusername = "";
//                                                            try {
//                                                                userId = Integer.parseInt(identification);
//                                                            } catch (Exception e) {
//                                                                mcusername = identification;
//                                                            }
//
//                                                            Date till;
//                                                            try {
//                                                                till = PunishmentData.getTillDateFromDurationString(duration);
//                                                            } catch (Exception e) {
//                                                                Chat.sendPrivateMessageToSelfError(e.getMessage());
//                                                                return 0;
//                                                            }
//                                                            sendPacket(new PunishUserPacket(PunishmentData.clientDefaultSetup(PunishmentData.Type.BLACKLIST, userId, APIUtils.getMcUUIDbyUsername(mcusername), till, reason)));
//                                                            return 1;
//                                                        })
//                                                )
//                                        )
//                                )
//                        )
//                );/*bpunish*/

                dispatcher.register(literal("bgetinfo")
                        .then(argument("userId/mcusername", StringArgumentType.string())
                                .executes((context) -> {
                                    String identification = StringArgumentType.getString(context, "userId/mcusername");
                                    sendPacket(new InternalCommandPacket(InternalCommandPacket.GET_USER_INFO, new String[]{identification}));
                                    return 1;
                                })
                        )
                );/*getInfo*/
            }
            if (hasSplasher) {
                dispatcher.register(
                        literal("splashAnnounce")
                                .then(argument("lesswaste", BoolArgumentType.bool())
                                        .then(argument("extramessage", StringArgumentType.greedyString())
                                                .executes((context) -> {
                                                    String extramessage = StringArgumentType.getString(context, "extramessage");
                                                    boolean lessWaste = BoolArgumentType.getBool(context, "lesswaste");
                                                    splashAnnounce(extramessage, lessWaste);
                                                    return 1;
                                                })
                                        )
                                        .executes((context) -> {
                                            boolean lessWaste = BoolArgumentType.getBool(context, "lesswaste");
                                            splashAnnounce(BBsentials.splashConfig.defaultExtraMessage, lessWaste);
                                            return 1;
                                        })
                                )
                                .executes((context) -> {
                                            splashAnnounce(BBsentials.splashConfig.defaultExtraMessage, BBsentials.splashConfig.defaultUseLessWaste);
                                            return 1;
                                        }
                                )
                );/*SplashAnnounce*/
                dispatcher.register(
                        literal("requestpottimes")
                                .executes((context) -> {
                                    sendPacket(new InternalCommandPacket(InternalCommandPacket.REQUEST_POT_DURATION, new String[0]));
                                    return 1;
                                })
                );/*requestpottimes*/
                dispatcher.register(
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
                );/*getLeecher*/
            }
            if (hasAdmin) {
                dispatcher.register(
                        literal("bshutdown")
                                .then(argument("Reason", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"Emergency Shutdown", "System Shutdown", "Other"}, builder))
                                        .executes((context) -> {
                                            String reason = StringArgumentType.getString(context, "Reason");
                                            sendPacket(new InternalCommandPacket(InternalCommandPacket.SHUTDOWN_SERVER, new String[]{reason}));
                                            return 1;
                                        })
                                )
                );/*BBShutdown*/
                dispatcher.register(
                        literal("bsetmotd")
                                .then(argument("Message", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{""}, builder))
                                        .executes((context) -> {
                                            String message = StringArgumentType.getString(context, "Message").trim();
                                            sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_MOTD, new String[]{message}));
                                            return 1;
                                        })
                                )
                );/*BBServerMotd*/
            }
        });
    }

    public void splashAnnounce(String extramessage, boolean lessWaste) {
        String serverid = EnvironmentCore.utils.getServerId();
        if (serverid == null) {
            Chat.sendPrivateMessageToSelfError("Could not get the Server ID from Tablist.");
            return;
        }
        Integer hubNumber = BBsentials.temporaryConfig.getHubNumberFromCache(serverid);
        if (hubNumber == null) {
            Chat.sendPrivateMessageToSelfError("Cache is either outdated or missing the current hub. Open the Hub Selector and try again.");
            return;
        }
        Position playerPos = EnvironmentCore.utils.getPlayersPosition();
        SplashLocation loc = null;
        for (SplashLocation value : SplashLocations.values()) {
            if (value.getCoords().isInRange(playerPos, 10)) {
                loc = value.getSplashLocation();
            }
        }
        if (loc == null)
            loc = new SplashLocation(new Position(playerPos.x, playerPos.y + 1, playerPos.z), null);
        try {
            sendPacket(new SplashNotifyPacket(new SplashData(BBsentials.generalConfig.getUsername(), hubNumber, loc, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste, serverid)));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
}
