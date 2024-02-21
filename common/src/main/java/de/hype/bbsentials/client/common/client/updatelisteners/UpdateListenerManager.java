package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.ChChestItems;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChestLobbyData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateListenerManager {
    public static BBsentialConnection connection;
    public static SplashStatusUpdateListener splashStatusUpdateListener;
    public static ChChestUpdateListener chChestUpdateListener;
    public static Map<Integer, ChestLobbyData> lobbies;

    public static void init() {
        splashStatusUpdateListener = new SplashStatusUpdateListener(null);
        lobbies = new HashMap<>();
        chChestUpdateListener = new ChChestUpdateListener(null);
        ServerSwitchTask.onServerJoinTask(UpdateListenerManager::permanentCheck, true);

    }

    public static void permanentCheck() {
        String serverId = EnvironmentCore.utils.getServerId();
        for (Map.Entry<Integer, ChestLobbyData> entry : lobbies.entrySet()) {
            if (!entry.getValue().serverId.equals(serverId)) continue;
            chChestUpdateListener = new ChChestUpdateListener(entry.getValue());
            chChestUpdateListener.sendUpdatePacket();
            chChestUpdateListener.run();
        }
    }

    public static void onChLobbyDataReceived(ChestLobbyData data) {
        ChestLobbyData oldData = lobbies.get(data.lobbyId);
        if (oldData == null) {
            if (data.getStatus().equalsIgnoreCase("Closed") || data.getStatus().equalsIgnoreCase("Left")) {
                lobbies.remove(data);
                return;
            }
            lobbies.put(data.lobbyId, data);
            if (BBsentialConnection.isCommandSafe(data.bbcommand)) {
                BBsentials.executionService.execute(UpdateListenerManager::permanentCheck);
                if (showChChest(data.chests.get(0).items)) {
                    String tellrawText = ("{\"text\":\"BB: @username found @item in a chest at (@coords). Click here to get a party invite @extramessage\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}");
                    tellrawText = tellrawText.replace("@username", data.contactMan);
                    tellrawText = tellrawText.replace("@item", Arrays.stream(data.chests.get(0).items)
                            .map(ChChestItem::getDisplayName)
                            .collect(Collectors.toList())
                            .toString());
                    tellrawText = tellrawText.replace("@coords", data.chests.get(0).coords.toString());
                    tellrawText = tellrawText.replace("@inviteCommand", data.bbcommand);
                    if (!(data.extraMessage == null || data.extraMessage.isEmpty())) {
                        tellrawText = tellrawText.replace("@extramessage", " : " + data.extraMessage);
                    }
                    Chat.sendPrivateMessageToSelfText(Message.tellraw(tellrawText));
                }
                try {
                    if (EnvironmentCore.utils.getServerId().equalsIgnoreCase(data.serverId)) {
                        ServerSwitchTask.onServerLeaveTask(() -> {
                            chChestUpdateListener.setStatus(StatusConstants.LEFT);
                        }, false);
                    }
                } catch (Exception ignored) {
                }
            }
            else {
                Chat.sendPrivateMessageToSelfFatal("Potentially dangerous packet detected. Action Command: " + data.bbcommand);
            }
        }
        else {
            if (chChestUpdateListener.isInLobby.get()) {
                chChestUpdateListener.updateLobby(data);
            }
        }
    }

    public static boolean showChChest(ChChestItem[] items) {
        if (BBsentials.chChestConfig.allChChestItem) return true;
        for (ChChestItem item : items) {
            if (BBsentials.chChestConfig.customChChestItem && item.isCustom()) return true;
            if (BBsentials.chChestConfig.allRoboPart && item.isRoboPart()) return true;
            if (BBsentials.chChestConfig.prehistoricEgg && item.equals(ChChestItems.PrehistoricEgg))
                return true;
            if (BBsentials.chChestConfig.pickonimbus2000 && item.equals(ChChestItems.Pickonimbus2000))
                return true;
            if (BBsentials.chChestConfig.controlSwitch && item.equals(ChChestItems.ControlSwitch)) return true;
            if (BBsentials.chChestConfig.electronTransmitter && item.equals(ChChestItems.ElectronTransmitter))
                return true;
            if (BBsentials.chChestConfig.robotronReflector && item.equals(ChChestItems.RobotronReflector))
                return true;
            if (BBsentials.chChestConfig.superliteMotor && item.equals(ChChestItems.SuperliteMotor))
                return true;
            if (BBsentials.chChestConfig.syntheticHeart && item.equals(ChChestItems.SyntheticHeart))
                return true;
            if (BBsentials.chChestConfig.flawlessGemstone && item.equals(ChChestItems.FlawlessGemstone))
                return true;
            if (BBsentials.chChestConfig.jungleHeart && item.equals(ChChestItems.JungleHeart)) return true;
        }
        return false;
    }

}
