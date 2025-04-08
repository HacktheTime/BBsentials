package de.hype.bingonet.environment.addonpacketconfig;

import com.google.gson.Gson;
import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.client.CustomGson;
import de.hype.bingonet.client.common.client.socketAddons.AddonHandler;

import java.util.function.Consumer;


public class AddonPacketUtils {
    public static final Gson gson = CustomGson.createNotPrettyPrinting();

    public static String parsePacketToJson(AbstractAddonPacket packet) {
        return gson.toJson(packet).replace("\n", "/n");
    }

    public static <T extends AbstractAddonPacket> void tryToProcessPacket(AddonPacket<T> addonPacket, String rawJson) {
        Class<T> clazz = addonPacket.getClazz();
        Consumer<T> consumer = addonPacket.getConsumer();
        T abstractPacket = gson.fromJson(rawJson.replace("/n", "\n"), clazz);
        consumer.accept(abstractPacket);
    }

    private static void showError(Throwable t, String errorMessage) {
        System.out.println(errorMessage + " because of: " + t.getClass().getSimpleName() + ":  " + t.getMessage());
        new Error(errorMessage, t).printStackTrace();
    }

    public static <T extends AbstractAddonPacket> T getAsPacket(String message, Class<T> clazz) {
        if (!message.contains(".")) return null;
        String packetName = message.split("\\.")[0];
        String rawJson = message.substring(packetName.length() + 1);
        if (!packetName.equals(clazz.getSimpleName())) {
            try {
                T parsedPacket = gson.fromJson(rawJson.replace("/n", "\n"), clazz);
                return parsedPacket;
            } catch (Throwable t) {
                showError(t, "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown Addon packet: " + packetName + "'"), errorMessage);
        return null;
    }

    public static boolean isPacket(String message, Class<? extends AbstractAddonPacket> clazz) {
        if (!message.contains(".")) return false;
        String packetName = message.split("\\.")[0];
        if (packetName.equals(clazz.getSimpleName())) {
            return true;
        }
        return false;
    }

    public static boolean isPacket(String message) {
        if (!message.contains(".")) return false;
        String packetName = message.split("\\.")[0];
        for (Class<? extends AbstractAddonPacket> packetClass : AddonPacketManager.getAllPacketClasses()) {
            if (!packetName.equals(packetClass.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static <T extends AbstractAddonPacket> boolean handleIfPacket(AddonHandler connection, String message) {
        //Return = is Packet
        if (!message.contains(".")) return false;
        String packetName = message.split("\\.")[0];
        String rawJson = message.substring(packetName.length() + 1);
        AddonPacketManager manager = new AddonPacketManager(connection);
        for (AddonPacket<? extends AbstractAddonPacket> addonPacket : manager.getPackets()) {
            if (!packetName.equals(addonPacket.getClazz().getSimpleName())) continue;
            try {
                if (BingoNet.socketAddonConfig.addonDebug)
                    Chat.sendPrivateMessageToSelfDebug(packetName + ":" + rawJson);
                tryToProcessPacket(addonPacket, rawJson);
                return true;
            } catch (Throwable t) {
                showError(t, "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown Addon packet: " + packetName + "'"), errorMessage);
        return false;
    }

    public static class APIException extends Error {

        public APIException(String errorMessage, Throwable t) {
            super(errorMessage, t);
        }

        public APIException(String errorMessage) {
            super(errorMessage);
        }
    }
}
