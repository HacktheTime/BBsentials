package de.hype.bbsentials.fabric;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.config.ConfigManager;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.WaypointRoute;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.fabric.command.ClientCommandManager;
import de.hype.bbsentials.fabric.command.Commands;
import de.hype.bbsentials.fabric.command.argumentTypes.SackMaterialArgumentType;
import de.hype.bbsentials.fabric.command.argumentTypes.SkyblockItemIdArgumentType;
import de.hype.bbsentials.fabric.command.argumentTypes.SkyblockRecipeArgumentType;
import de.hype.bbsentials.fabric.command.argumentTypes.SkyblockWarpArgumentType;
import de.hype.bbsentials.fabric.mixins.helperclasses.RenderingDefinitions;
import de.hype.bbsentials.fabric.numpad.NumPadCodes;
import de.hype.bbsentials.fabric.screens.BBsentialsConfigScreenFactory;
import de.hype.bbsentials.fabric.screens.RouteConfigScreen;
import de.hype.bbsentials.fabric.screens.RoutesConfigScreen;
import de.hype.bbsentials.fabric.screens.WaypointsConfigScreen;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.objects.RenderInformation;
import dev.xpple.clientarguments.arguments.CBlockPosArgument;
import net.fabricmc.api.ClientModInitializer;
import de.hype.bbsentials.fabric.command.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.hype.bbsentials.client.common.client.BBsentials.*;
import static de.hype.bbsentials.client.common.objects.WaypointRoute.waypointRouteDirectory;
import static de.hype.bbsentials.fabric.command.ClientCommandManager.argument;
import static de.hype.bbsentials.fabric.command.ClientCommandManager.literal;

public class ModInitialiser implements ClientModInitializer {
    public static NumPadCodes codes;
    public static KeyBinding openWikiKeybind;
    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher) -> {
            dispatcher.register(ClientCommandManager.literal("socialoptions")
                    .then(ClientCommandManager.argument("playername", StringArgumentType.greedyString())
                            .executes((context) -> {
                                String[] parameters = StringArgumentType.getString(context, "playername").trim().split(" ", 3);
                                Chat.sendPrivateMessageToSelfDebug(String.join(" ", parameters));
                                String tellrawjson = "";
                                if (parameters.length >= 3) {
                                    if (parameters[0].equals("sb")) {
                                        tellrawjson = "[\"\",{\"text\":\"\n\n$username\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Click to copy the username\",\"color\":\"blue\"}]}},\" \",{\"text\":\"[Party]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to party player\"]}},\" \",{\"text\":\"[Invite]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/invite $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to invite them to visit your private island/garden\"]}},\" \",{\"text\":\"[Visit]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/visit $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to open the visit menu for that user\"]}},\" \",{\"text\":\"[creport]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/creport $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to report the player for chat (public)\"]}},\" \",{\"text\":\"[Ignore add]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to ignore add the user\"]}},\" \",{\"text\":\"[Copy content]\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$messagecontent\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the message content\"]}},\" \",{\"text\":\"[Copy message]\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the exact message\"]}},\" \",{\"text\":\"[Msg]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/msg $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to msg the user\"]}},\" \",{\"text\":\"[Sky shiiyu]\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://sky.shiiyu.moe/stats/$username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to open the users sky shiiyu page.\"]}},\"\\n\"]";
                                        //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":true,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the username"}],"text":"$username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to party player"}],"text":"[Party]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to invite them to visit your private island/garden"}],"text":"[Invite]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/visit $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the visit menu for that user"}],"text":"[Visit]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"suggest_command","click_event_value":"/creport $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to report the player for chat (public)"}],"text":"[creport]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/ignore add $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to ignore add the user"}],"text":"[Ignore add]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$messagecontent","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the message content"}],"text":"[Copy content]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the exact message"}],"text":"[Copy message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/msg $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to msg the user"}],"text":"[Msg]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"aqua","insertion":"","click_event_type":"open_url","click_event_value":"https://sky.shiiyu.moe/stats/$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the users sky shiiyu page."}],"text":"[Sky shiiyu]"}],"command":"%s","jtemplate":"tellraw"}
                                    }
                                    else if (parameters[0].equals("guild")) {
                                        tellrawjson = "[\"\",{\"text\":\"\n\n$username\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Click to copy the username\",\"color\":\"blue\"}]}},\" \",{\"text\":\"[Party]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to party player\"]}},\" \",{\"text\":\"[SB Options]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/socialoptions sb $username $message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to open the SB options\"]}},\" \",{\"text\":\"[Member info]\",\"color\":\"dark_aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/g member $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Show guild info about the user.\"]}},\" \",{\"text\":\"[Copy content]\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$messagecontent\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the message content\"]}},\" \",{\"text\":\"[Copy message]\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the exact message\"]}},\" \",{\"text\":\"[Msg]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/msg $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to msg the user\"]}},\" \",{\"text\":\"[Ignore add]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to ignore add the user\"]}},\"\\n\",{\"text\":\"G Admin: \",\"color\":\"dark_red\"},{\"text\":\"[kick]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/g kick $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"suggest the command to kick the user\"]}},\" \",{\"text\":\"[mute]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/g mute $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Suggest a mute command for the user.\"]}},\" \",{\"text\":\"[promote]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/g promote $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to promote the user\"]}},\" \",{\"text\":\"[demote]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/g demote $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to demote the user\"]}}]";
                                        //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":true,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the username"}],"text":"$username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to party player"}],"text":"[Party]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/socialoptions sb $username $message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the SB options"}],"text":"[SB Options]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_aqua","insertion":"","click_event_type":"run_command","click_event_value":"/g member $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Show guild info about the user."}],"text":"[Member info]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$messagecontent","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the message content"}],"text":"[Copy content]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the exact message"}],"text":"[Copy message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/msg $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to msg the user"}],"text":"[Msg]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/ignore add $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to ignore add the user"}],"text":"[Ignore add]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"\n"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"G Admin: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/g kick $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"suggest the command to kick the user"}],"text":"[kick]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/g mute $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Suggest a mute command for the user."}],"text":"[mute]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_green","insertion":"","click_event_type":"suggest_command","click_event_value":"/g promote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to promote the user"}],"text":"[promote]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/g demote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to demote the user"}],"text":"[demote]"}],"command":"%s","jtemplate":"tellraw"}
                                    }
                                    else if (parameters[0].equals("party")) {
                                        if (partyConfig.isPartyLeader) {
                                            tellrawjson = "[\"\",{\"text\":\"\n\n$username\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Click to copy the username\",\"color\":\"blue\"}]}},\" \",{\"text\":\"[Party]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to party player\"]}},\" \",{\"text\":\"[SB Options]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/socialoptions sb $username $message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to open the SB options\"]}},\" \",{\"text\":\"[Copy content]\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$messagecontent\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the message content\"]}},\" \",{\"text\":\"[Copy message]\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the exact message\"]}},\" \",{\"text\":\"[Msg]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/msg $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to msg the user\"]}},\" \",{\"text\":\"[Ignore add]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to ignore add the user\"]}},\"\\n\",\"\\n\",{\"text\":\"P Leader: \",\"color\":\"dark_red\"},{\"text\":\"[kick]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p kick $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"suggest the command to kick the user\"]}},\" \",{\"text\":\"[transfer]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/p transfer $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Suggest a mute command for the user.\"]}},\" \",{\"text\":\"[promote]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p promote $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to promote the user\"]}},\" \",{\"text\":\"[demote]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p demote $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to demote the user\"]}}]";
                                            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":true,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the username"}],"text":"$username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to party player"}],"text":"[Party]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/socialoptions sb $username $message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the SB options"}],"text":"[SB Options]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$messagecontent","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the message content"}],"text":"[Copy content]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the exact message"}],"text":"[Copy message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/msg $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to msg the user"}],"text":"[Msg]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/ignore add $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to ignore add the user"}],"text":"[Ignore add]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"\n"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"\n"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"P Leader: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"run_command","click_event_value":"/p kick $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"suggest the command to kick the user"}],"text":"[kick]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"suggest_command","click_event_value":"/p transfer $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Suggest a mute command for the user."}],"text":"[transfer]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_green","insertion":"","click_event_type":"run_command","click_event_value":"/p promote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to promote the user"}],"text":"[promote]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"run_command","click_event_value":"/p demote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to demote the user"}],"text":"[demote]"}],"command":"%s","jtemplate":"tellraw"}
                                        }
                                        else {
                                            tellrawjson = "[\"\",{\"text\":\"\n\n$username\",\"underlined\":true,\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[{\"text\":\"Click to copy the username\",\"color\":\"blue\"}]}},\" \",{\"text\":\"[Party]\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/p invite $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to party player\"]}},\" \",{\"text\":\"[SB Options]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/socialoptions sb $username $message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to open the SB options\"]}},\" \",{\"text\":\"[Copy content]\",\"color\":\"gray\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$messagecontent\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the message content\"]}},\" \",{\"text\":\"[Copy message]\",\"color\":\"blue\",\"clickEvent\":{\"action\":\"copy_to_clipboard\",\"value\":\"$message\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to copy the exact message\"]}},\" \",{\"text\":\"[Msg]\",\"color\":\"dark_purple\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/msg $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to msg the user\"]}},\" \",{\"text\":\"[Ignore add]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/ignore add $username\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"Click to ignore add the user\"]}}]";
                                            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":true,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the username"}],"text":"$username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to party player"}],"text":"[Party]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/socialoptions sb $username $message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the SB options"}],"text":"[SB Options]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$messagecontent","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the message content"}],"text":"[Copy content]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the exact message"}],"text":"[Copy message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/msg $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to msg the user"}],"text":"[Msg]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/ignore add $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to ignore add the user"}],"text":"[Ignore add]"}],"command":"%s","jtemplate":"tellraw"}
                                        }
                                        //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":true,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the username"}],"text":"$username"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"run_command","click_event_value":"/p invite $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to party player"}],"text":"[Party]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"run_command","click_event_value":"/socialoptions sb $username $message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to open the SB options"}],"text":"[SB Options]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$messagecontent","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the message content"}],"text":"[Copy content]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"blue","insertion":"","click_event_type":"copy_to_clipboard","click_event_value":"$message","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to copy the exact message"}],"text":"[Copy message]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"suggest_command","click_event_value":"/msg $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to msg the user"}],"text":"[Msg]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/ignore add $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to ignore add the user"}],"text":"[Ignore add]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"\n"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_red","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"P Leader: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_purple","insertion":"","click_event_type":"run_command","click_event_value":"/p kick $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"suggest the command to kick the user"}],"text":"[kick]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"suggest_command","click_event_value":"/p mute $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Suggest a mute command for the user."}],"text":"[mute]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_green","insertion":"","click_event_type":"run_command","click_event_value":"/p promote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to promote the user"}],"text":"[promote]"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":" "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"red","insertion":"","click_event_type":"run_command","click_event_value":"/p demote $username","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Click to demote the user"}],"text":"[demote]"}],"command":"%s","jtemplate":"tellraw"}
                                    }
                                    tellrawjson = tellrawjson.replace("$username", parameters[1]);
                                    int doublepointIndex = parameters[2].indexOf(":");
                                    if (doublepointIndex != -1) {
                                        tellrawjson = tellrawjson.replace("$messagecontent", parameters[2].substring(doublepointIndex + 1).trim());
                                    }
                                    tellrawjson = tellrawjson.replace("$message", parameters[2]);
                                }
                                if (tellrawjson.isEmpty())
                                    Chat.sendCommand("/socialoptions " + String.join(" ", parameters));
                                else {
                                    Chat.sendPrivateMessageToSelfText(Message.tellraw(tellrawjson));
                                }
                                return 1;
                            })));
        });//sbsocialoptions
        de.hype.bbsentials.fabric.command.ClientCommandRegistrationCallback.EVENT.register(dispatcher -> {
            dispatcher.replaceRegister(literal("creport")
                    .then(argument("Player_Name", StringArgumentType.string())
                            .executes((context) -> {
                                String playerName = StringArgumentType.getString(context, "Player_Name");
                                BBsentials.sender.addSendTask("/creport " + playerName, 0);
                                BBsentials.temporaryConfig.alreadyReported.add(playerName);
                                return 1;
                            })));
            dispatcher.replaceRegister(literal("warp")
                    .then(argument("warp", SkyblockWarpArgumentType.warptype())
                            .executes(context -> {
                                BBsentials.sender.addImmediateSendTask("/warp " + SkyblockWarpArgumentType.getWarpString(context, "warp"));
                                return 1;
                            })
                    ));
            dispatcher.replaceRegister(literal("viewrecipe")
                    .then(argument("itemid", SkyblockRecipeArgumentType.itemidtype())
                            .executes(context -> {
                                BBsentials.sender.addImmediateSendTask("/viewrecipe " + SkyblockRecipeArgumentType.getItemId(context, "itemid"));
                                return 1;
                            })
                    ));
            dispatcher.replaceRegister(literal("gfs")
                    .then(argument("itemid", SackMaterialArgumentType.materialidtype())
                            .then(argument("count", IntegerArgumentType.integer(0))
                                    .executes(context -> {
                                        BBsentials.sender.addImmediateSendTask("/gfs " + SackMaterialArgumentType.getItemId(context, "itemid") + IntegerArgumentType.getInteger(context, "count"));
                                        return 1;
                                    }))
                    ));
            dispatcher.replaceRegister(literal("wiki")
                    .then(argument("itemid", SkyblockItemIdArgumentType.itemidtype())
                            .executes(context -> {
                                BBsentials.sender.addImmediateSendTask("/wiki " + SkyblockItemIdArgumentType.getItemId(context, "itemid"));
                                return 1;
                            })
                    ));
            dispatcher.register(literal("getLobbyTime")
                    .executes((context) -> {
                        Chat.sendPrivateMessageToSelfSuccess("Day: " + EnvironmentCore.utils.getLobbyDay());
                        return 1;
                    }));
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher) -> {
            dispatcher.register(ClientCommandManager.literal("bbi")
                            .then(ClientCommandManager.literal("reconnect")
                                    .executes((context) -> {
                                        connectToBBserver();
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("disconnect")
                                    .executes((context) -> {
                                        connection.close();
                                        Chat.sendPrivateMessageToSelfInfo("Disconnected");
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("reconnect-stable-server")
                                    .executes((context) -> {
                                        connectToBBserver(false);
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("reconnect-test-server")
                                    .executes((context) -> {
                                        connectToBBserver(true);
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("reconnect-local-test-server")
                                    .executes((context) -> {
                                        executionService.execute(() -> {
                                            try {
                                                Socket test = new Socket("localhost", 5011);
                                                test.close();
                                            } catch (Exception e) {
                                                Chat.sendPrivateMessageToSelfError("Could not reach a local test Server. Do you have one running?");
                                                return;
                                            }

                                            if (connection != null) {
                                                connection.close();
                                            }
                                            bbthread = new Thread(() -> {
                                                connection = new BBsentialConnection();
                                                coms = new de.hype.bbsentials.client.common.client.commands.Commands();
                                                connection.connect("localhost", 5011);
                                            });
                                            bbthread.start();

                                        });
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("config")
                                    .then(ClientCommandManager.argument("category", StringArgumentType.string())
                                            .suggests((context, builder) -> {
                                                // Provide tab-completion options for configManager subfolder
                                                return CommandSource.suggestMatching(new String[]{"saveAll", "reset", "load"}, builder);
                                            }).executes((context) -> {
                                                String category = StringArgumentType.getString(context, "category");
                                                switch (category) {
                                                    case "saveAll":
                                                        ConfigManager.saveAll();
                                                        Chat.sendPrivateMessageToSelfSuccess("Saved configs successfully");
                                                        break;
                                                    case "load":
                                                        ConfigManager.reloadAllConfigs();
                                                        break;
                                                    case "reset":
                                                        // Reset logic here
                                                        break;
                                                }
                                                return 1;
                                            }))
                                    .then(ClientCommandManager.literal("set-value")
                                            .then(ClientCommandManager.argument("className", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        // Provide tab-completion options for classes
                                                        List<String> classNames = ConfigManager.getLoadedConfigClasses().stream().map(Class::getSimpleName).toList();
                                                        // Replace with your own logic to retrieve class names
                                                        return CommandSource.suggestMatching(classNames, builder);
                                                    })
                                                    .then(ClientCommandManager.argument("variableName", StringArgumentType.string())
                                                            .suggests((context, builder) -> {
                                                                // Provide tab-completion options for variable names
                                                                List<String> variableNames = new ArrayList<>();
                                                                try {
                                                                    variableNames = List.of(Chat.getVariableNames("de.hype.bbsentials.client.common.config", StringArgumentType.getString(context, "className")));
                                                                } catch (Exception e) {
                                                                    context.getSource().sendError(Text.of("§cCouldnt locate the specified Classes Variables. Is the Class correct?"));
                                                                }
                                                                return CommandSource.suggestMatching(variableNames, builder);
                                                            })
                                                            .then(ClientCommandManager.argument("variableValue", StringArgumentType.string())
                                                                    .executes((context) -> {
                                                                        // Handle "variableName" and "variableValue" logic here
                                                                        String variableName = StringArgumentType.getString(context, "variableName");
                                                                        String variableValue = StringArgumentType.getString(context, "variableValue");
                                                                        try {
                                                                            if (!variableName.toLowerCase().contains("dev") || generalConfig.hasBBRoles("dev")) {
                                                                                Chat.setVariableValue(StringArgumentType.getString(context, "className"), variableName, variableValue);
                                                                            }
                                                                            ConfigManager.saveAll();
                                                                        } catch (ClassNotFoundException |
                                                                                 NoSuchFieldException |
                                                                                 IllegalAccessException |
                                                                                 InstantiationException |
                                                                                 InvocationTargetException |
                                                                                 NoSuchMethodException e) {
                                                                            context.getSource().sendError(Text.of("§cInvalid variable or value"));
                                                                        }
                                                                        return 1;
                                                                    })))))
                                    .then(ClientCommandManager.literal("get-value")
                                            .then(ClientCommandManager.argument("className", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        // Provide tab-completion options for classes
                                                        List<String> classNames = ConfigManager.getLoadedConfigClasses().stream().map(Class::getSimpleName).toList();
                                                        // Replace with your own logic to retrieve class names
                                                        return CommandSource.suggestMatching(classNames, builder);
                                                    })
                                                    .then(ClientCommandManager.argument("variableName", StringArgumentType.string())
                                                            .suggests((context, builder) -> {
                                                                // Provide tab-completion options for variable names
                                                                List<String> variableNames;
                                                                variableNames = List.of(Chat.getVariableNames("de.hype.bbsentials.client.common.config", StringArgumentType.getString(context, "className")));
                                                                return CommandSource.suggestMatching(variableNames, builder);
                                                            })
                                                            .executes((context) -> {
                                                                // Handle "variableName" and "variableValue" logic here
                                                                String variableName = StringArgumentType.getString(context, "variableName");
                                                                try {
                                                                    Chat.getVariableValue(StringArgumentType.getString(context, "className"), variableName);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    context.getSource().sendError(Text.of("§cInvalid variable or value"));
                                                                }
                                                                return 1;
                                                            }))).executes((context) -> {
                                                return 1;
                                            })
                                    )
                            )
                            .then(ClientCommandManager.literal("waypoint")
                                            .then(ClientCommandManager.literal("add")
                                                    .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                                            .then(ClientCommandManager.argument("position", CBlockPosArgument.blockPos())
                                                                    .then(ClientCommandManager.argument("deleteonserverswap", BoolArgumentType.bool())
                                                                            .then(ClientCommandManager.argument("visible", BoolArgumentType.bool())
                                                                                    .then(ClientCommandManager.argument("maxrenderdistance", IntegerArgumentType.integer())
                                                                                            .then(ClientCommandManager.argument("customtexture", StringArgumentType.string())
                                                                                                    .executes(this::createWaypointFromCommandContext)
                                                                                            )
                                                                                            .executes(this::createWaypointFromCommandContext)
                                                                                    )
                                                                            )
                                                                    )
                                                                    .executes(this::createWaypointFromCommandContext)
                                                            )
                                                    )
                                            )
                                            .then(ClientCommandManager.literal("remove")).then(ClientCommandManager.argument("waypointid", IntegerArgumentType.integer()).executes((context -> {
                                                int wpId = IntegerArgumentType.getInteger(context, "waypointid");
                                                return (Waypoints.waypoints.remove(wpId) != null) ? 1 : 0;
                                            })))
//                            .then(ClientCommandManager.literal("clear").executes((context -> {
//                                 Waypoints.waypoints.clear();
//                                 return 1;
//                            })))
                                            .then(ClientCommandManager.literal("setvisibility")).then(ClientCommandManager.argument("waypointid", IntegerArgumentType.integer())
                                                    .then(ClientCommandManager.argument("visible", BoolArgumentType.bool()).executes((context -> {
                                                        int wpId = IntegerArgumentType.getInteger(context, "waypointid");
                                                        boolean visible = BoolArgumentType.getBool(context, "setvisibility");
                                                        Waypoints waypoint = Waypoints.waypoints.get(wpId);
                                                        if (waypoint == null) {
                                                            context.getSource().sendError(Text.of("No Waypoint on that ID found"));
                                                            return 0;
                                                        }
                                                        if (waypoint.visible == visible) {
                                                            Chat.sendPrivateMessageToSelfInfo("Nothing changed. Waypoint visibility was that state already");
                                                            return 1;
                                                        }
                                                        else {
                                                            waypoint.visible = visible;
                                                            Chat.sendPrivateMessageToSelfSuccess("Nothing changed. Waypoint visibility was that state already");
                                                            return 1;
                                                        }
                                                    })))
                                                    .then(ClientCommandManager.literal("info")).then(ClientCommandManager.argument("waypointid", IntegerArgumentType.integer()).executes((context -> {
                                                        int wpId = IntegerArgumentType.getInteger(context, "waypointid");
                                                        try {
                                                            Chat.sendPrivateMessageToSelfInfo(Waypoints.waypoints.get(wpId).getFullInfoString());
                                                            return 1;
                                                        } catch (NullPointerException ignored) {
                                                            return 0;
                                                        }
                                                    })))
                                                    .then(ClientCommandManager.literal("list").executes((context -> {
                                                                Waypoints.waypoints.forEach(((integer, waypoint) -> {
                                                                    Chat.sendPrivateMessageToSelfInfo(waypoint.getMinimalInfoString());
                                                                }));
                                                                return 1;
                                                            }))
                                                    )
                                            )
                                            .executes((context -> {
                                                executionService.schedule(() -> {
                                                    MinecraftClient client = MinecraftClient.getInstance();
                                                    client.execute(() -> client.setScreen(RouteConfigScreen.openCurrent(new WaypointsConfigScreen(MinecraftClient.getInstance().currentScreen))));
                                                }, 10, TimeUnit.MILLISECONDS);
                                                return 1;
                                            }))
                            ).then(ClientCommandManager.literal("route")
                                    .executes((context -> {
                                        executionService.schedule(() -> {
                                            MinecraftClient client = MinecraftClient.getInstance();
                                            client.execute(() -> client.setScreen(new RoutesConfigScreen(MinecraftClient.getInstance().currentScreen)));
                                        }, 10, TimeUnit.MILLISECONDS);
                                        return 1;
                                    }))
                                    .then(ClientCommandManager.literal("current")
                                            .executes((context -> {
                                                executionService.schedule(() -> {
                                                    MinecraftClient client = MinecraftClient.getInstance();
                                                    client.execute(() -> client.setScreen(RouteConfigScreen.openCurrent(new RoutesConfigScreen(MinecraftClient.getInstance().currentScreen))));
                                                }, 10, TimeUnit.MILLISECONDS);
                                                return 1;
                                            }))
                                    )
                                    .then(ClientCommandManager.literal("load")
                                            .then(ClientCommandManager.argument("fileName", StringArgumentType.string())
                                                    .suggests(((context, builder) -> {
                                                        if (waypointRouteDirectory.exists() && waypointRouteDirectory.isDirectory()) {
                                                            List<String> routeNames = Arrays.stream(waypointRouteDirectory.listFiles())
                                                                    .filter(file -> file.isFile() && file.getName().endsWith(".json"))
                                                                    .map(file -> file.getName().replace(".json", ""))
                                                                    .collect(Collectors.toList());

                                                            return CommandSource.suggestMatching(routeNames, builder);
                                                        }
                                                        return Suggestions.empty();
                                                    })).then(ClientCommandManager.argument("startingnodeid", IntegerArgumentType.integer())
                                                            .executes((context -> {
                                                                try {
                                                                    WaypointRoute.loadRoute(StringArgumentType.getString(context, "fileName") + ".json").setCurentNode(IntegerArgumentType.getInteger(context, "startingnodeid"));
                                                                    return 1;
                                                                } catch (Exception e) {
                                                                    return 0;
                                                                }
                                                            })))
                                                    .executes((context -> {
                                                        try {
                                                            WaypointRoute.loadRoute(StringArgumentType.getString(context, "fileName"));
                                                            return 1;
                                                        } catch (Exception e) {
                                                            return 0;
                                                        }
                                                    }))
                                            )
                                    )//TODO suggest names automatically and actual load code.
                                    .then(ClientCommandManager.literal("unload").executes((context) -> {
                                        temporaryConfig.route = null;
                                        Chat.sendPrivateMessageToSelfSuccess("Unloaded current route");
                                        return 1;
                                    }))
                                    .then(ClientCommandManager.literal("setCurrentNode")
                                            .then(ClientCommandManager.argument("number", IntegerArgumentType.integer())
                                                    .executes((context -> {
                                                        int id = IntegerArgumentType.getInteger(context, "number");
                                                        if (temporaryConfig.route == null) {
                                                            context.getSource().sendError(Text.of("No Route loaded"));
                                                            return 0;
                                                        }
                                                        if (id >= temporaryConfig.route.nodes.size() || id <= 0) {
                                                            context.getSource().sendError(Text.of("Out of Bounds. The number you specified is higher than the node count or lower than 1"));
                                                            return 0;
                                                        }
                                                        temporaryConfig.route.currentNode = id - 1;
                                                        return 1;
                                                    }))
                                            )
                                    )
                            )
            );

        }); //bbi
        KeyBinding devKeyBind = new KeyBinding("Open Mod Menu Config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_MULTIPLY, "BBsentials: Developing Tools");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (devKeyBind.wasPressed()) {
                MinecraftClient.getInstance().setScreen(BBsentialsConfigScreenFactory.create(MinecraftClient.getInstance().currentScreen));
            }
        });
        openWikiKeybind = new KeyBinding("Lookup Hovered Item in Wiki", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "BBsentials");
        KeyBindingHelper.registerKeyBinding(openWikiKeybind);

        KeyBinding promptKeyBind = new KeyBinding("Chat Prompt Yes / Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "BBsentials");
        KeyBindingHelper.registerKeyBinding(promptKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (promptKeyBind.wasPressed()) {
                ChatPrompt prompt = temporaryConfig.lastChatPromptAnswer;
                if (prompt != null) {
                    if (prompt.isAvailibel()) {
                        if (BBsentials.developerConfig.isDetailedDevModeEnabled()) {
                            Chat.sendPrivateMessageToSelfDebug(prompt.command);
                        }
                        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(prompt.getCommandAndCancel());
                    }
                }
            }
        });
        KeyBinding craftKeyBind = new KeyBinding("Craft", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "BBsentials");
        KeyBindingHelper.registerKeyBinding(craftKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (craftKeyBind.wasPressed())
                MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/craft");
        });
        KeyBinding petKeyBind = new KeyBinding("Open Pet Menu", InputUtil.Type.KEYSYM, -1, "BBsentials");
        KeyBindingHelper.registerKeyBinding(petKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (petKeyBind.wasPressed()) MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/pets");
        });
        KeyBinding tradesKeyBind = new KeyBinding("Trades Menu", InputUtil.Type.KEYSYM, -1, "BBsentials");
        KeyBindingHelper.registerKeyBinding(tradesKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (tradesKeyBind.wasPressed()) {
                MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/trades");
            }
        });
        KeyBinding pickUpStashKeybind = new KeyBinding("Pick up stash", InputUtil.Type.KEYSYM, -1, "BBsentials");
        KeyBindingHelper.registerKeyBinding(pickUpStashKeybind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (pickUpStashKeybind.wasPressed()) {
                MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/pickupstash");
            }
        });
//        for (int i = 1; i <= 9; i++) {
//            KeyBinding ecPageKeyBind = new KeyBinding("Ender Chest Page " + i, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1 + (i - 1), "BBsentials");
//            ecPageKeyBind);
//            int pageNum = i; // Capture the page number for lambda
//            ClientTickEvents.END_CLIENT_TICK.register(client -> {
//                if (ecPageKeyBind.wasPressed()) {
//                    getConfig().sender.addImmediateSendTask("/ec " + pageNum);
//                }
//            });
//        }
    } // KeyBinds

    @Override
    public void onInitializeClient() {
        System.out.println("BBsentials : onInit called");
        EnvironmentCore core = EnvironmentCore.fabric(new Utils(), new MCEvents(), new FabricChat(), new Commands(), new Options(), new DebugThread(), new FabricTextUtils());
        codes = new NumPadCodes();
        BBsentials.init();
        if (developerConfig.quickLaunch) {
            executionService.execute(() -> {
                while (!MinecraftClient.getInstance().isFinishedLoading()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
                MinecraftClient.getInstance().execute(this::joinHypixel);
            });
        }
        RenderingDefinitions.clearAndInitDefaults();
        if (generalConfig.hasBBRoles("dev")) {
            ServerSwitchTask.onServerJoinTask(() -> EnvironmentCore.debug.onServerJoin(), true);
            ServerSwitchTask.onServerLeaveTask(() -> EnvironmentCore.debug.onServerLeave(), true);
        }
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
            BBsentials.onServerJoin();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> {
            BBsentials.onServerLeave();
        });
        ServerSwitchTask.onServerJoinTask(() -> {
            if (visualConfig.doGammaOverride) {
                new Options().setGamma(10);
            }
            if (EnvironmentCore.utils.getUsername().toLowerCase().equals("p0is")) {
                funConfig.hub17To29Troll = true;
            }
        });
    }

    public int createWaypointFromCommandContext(CommandContext context) {
        String jsonName = StringArgumentType.getString(context, "name");
        jsonName = EnvironmentCore.utils.stringToTextJson(jsonName);
        BlockPos pos = CBlockPosArgument.getBlockPos(context, "position");
        Position position = new Position(pos.getX(), pos.getY(), pos.getZ());
        Boolean deleteOnServerSwap = true;
        Boolean visible = true;
        Integer maxRenderDist = 10000;
        try {
            deleteOnServerSwap = BoolArgumentType.getBool(context, "deleteonserverswap");
            visible = BoolArgumentType.getBool(context, "visible");
            maxRenderDist = IntegerArgumentType.getInteger(context, "maxrenderdistance");
        } catch (IllegalArgumentException ignored) {

        }
        String customTextureFull = null;
        String customTextureNameSpace = "";
        String customTexturePath = "";
        try {
            customTextureFull = StringArgumentType.getString(context, "customtexture");
            if (customTextureFull.contains(":")) {
                customTextureNameSpace = customTextureFull.split(":")[0];
                customTexturePath = customTextureFull.split(":")[1];
            }
            else {
                customTextureFull = customTexturePath;
            }
        } catch (Exception ignored) {

        }

        Waypoints waypoint = new Waypoints(position, jsonName, maxRenderDist, visible, deleteOnServerSwap, new RenderInformation(customTextureNameSpace, customTexturePath));
        return 1;
    }


    public void disconnectServer() {
        MinecraftClient.getInstance().disconnect();
    }

    public void joinHypixel() {
        String serverAddress = "mc.hypixel.net";
        MinecraftClient client = MinecraftClient.getInstance();
        ServerList serverList = new ServerList(client);
        serverList.loadFile();
        ServerInfo serverInfo = serverList.get(serverAddress);
        if (serverInfo == null) {
            serverInfo = new ServerInfo("Hypixel", serverAddress, ServerInfo.ServerType.OTHER);
            serverList.add(serverInfo, true);
            serverList.saveFile();
        }
        ServerAddress serverAddress2 = ServerAddress.parse(serverAddress);

        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, serverAddress2, serverInfo, true, null);
    }
}
