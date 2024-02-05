package de.hype.bbsentials.environment.packetconfig;

import com.google.gson.Gson;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.CustomGson;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;

import java.util.function.Consumer;


public class PacketUtils {
    public static final Gson gson = CustomGson.createNotPrettyPrinting();

    public static String parsePacketToJson(AbstractPacket packet) {
        return gson.toJson(packet).replace("\n","/n");
    }

    public static <T extends AbstractPacket> void tryToProcessPacket(Packet<T> packet, String rawJson) {
        Class<T> clazz = packet.getClazz();
        Consumer<T> consumer = packet.getConsumer();
        T abstractPacket = gson.fromJson(rawJson.replace("/n","\n"), clazz);
        consumer.accept(abstractPacket);
    }

    private static void showError(Throwable t, String errorMessage) {
        System.out.println(errorMessage + " because of: " + t.getClass().getSimpleName() + ":  " + t.getMessage());
        new Error(errorMessage, t).printStackTrace();
    }

    public static class APIException extends Error {

        public APIException(String errorMessage, Throwable t) {
            super(errorMessage, t);
        }

        public APIException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static <T extends AbstractPacket> T getAsPacket(String message, Class<T> clazz) {
        if (!message.contains(".")) return null;
        String packetName = message.split("\\.")[0];
        String rawJson = message.substring(packetName.length() + 1);
        if (!packetName.equals(clazz.getSimpleName())) {
            try {
                T parsedPacket = gson.fromJson(rawJson.replace("/n","\n"), clazz);
                return parsedPacket;
            } catch (Throwable t) {
                showError(t, "Could not process packet '" + packetName + "' from " + EnvironmentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process packet '" + packetName + "' from " + EnvironmentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown packet: " + packetName + "'"), errorMessage);
        return null;
    }

    public static boolean isPacket(String message, Class<? extends AbstractPacket> clazz) {
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
        for (Class<? extends AbstractPacket> packetClass : PacketManager.getAllPacketClasses()) {
            if (!packetName.equals(packetClass.getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public static <T extends AbstractPacket> boolean handleIfPacket(BBsentialConnection connection, String message) {
        //Return = is Packet
        if (!message.contains(".")) return false;
        String packetName = message.split("\\.")[0];
        String rawJson = message.substring(packetName.length() + 1);
        PacketManager manager = new PacketManager(connection);
        for (Packet<? extends AbstractPacket> packet : manager.getPackets()) {
            if (!packetName.equals(packet.getClazz().getSimpleName())) continue;
            try {
                if (BBsentials.developerConfig.isDetailedDevModeEnabled()) Chat.sendPrivateMessageToSelfDebug(packetName+":"+rawJson);
                tryToProcessPacket(packet, rawJson);
                return true;
            }catch (RuntimeException e){
                throw e;
            }catch (Exception t) {
                showError(t, "Could not process packet '" + packetName + "' from " + EnvironmentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process packet '" + packetName + "' from " + EnvironmentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown packet: " + packetName + "'"), errorMessage);
        return false;
    }
}
