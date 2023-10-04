package de.hype.bbsentials.packets;

import com.google.gson.Gson;
import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import de.hype.bbsentials.client.CustomGson;
import de.hype.bbsentials.communication.BBsentialConnection;

import java.util.function.Consumer;


public class PacketUtils {
    public static final Gson gson = CustomGson.create();

    public static String parsePacketToJson(AbstractPacket packet) {
        return gson.toJson(packet);
    }

    public static <T extends AbstractPacket> void tryToProcessPacket(Packet<T> packet, String rawJson) {
        Class<T> clazz = packet.getClazz();
        Consumer<T> consumer = packet.getConsumer();
        T abstractPacket = gson.fromJson(rawJson, clazz);
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
                T parsedPacket = gson.fromJson(rawJson, clazz);
                return parsedPacket;
            } catch (Throwable t) {
                showError(t, "Could not process packet '" + packetName + "' from " + EnviromentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process packet '" + packetName + "' from " + EnviromentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown packet: " + packetName + "'"), errorMessage);
        return null;
    }

//    public static <T extends AbstractPacket> T getAsPacket(String message) {
//        if (!message.contains(".")) return null;
//        String packetName = message.split("\\.")[0];
//        String rawJson = message.substring(packetName.length() + 1);
//
//        for (Class<? extends AbstractPacket> clazz : PacketManager.getAllPacketClasses()) {
//            if (packetName.equals(clazz.getSimpleName())) {
//                try {
//                    if (clazz.isAssignableFrom(clazz)) {
//                        //TODO the problem
//                        T parsedPacket = (T) gson.fromJson(rawJson, clazz);
//                        return parsedPacket;
//                    }
//                    else {
//                        return null;
//                    }
//                } catch (Throwable t) {
//                    showError(t, "Could not process packet '" + packetName + "' from " + EnviromentPacketConfig.notEnviroment);
//                }
//            }
//        }
//        return null;
//    }

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
                if (BBsentials.getConfig().isDetailedDevModeEnabled()) Chat.sendPrivateMessageToSelfDebug(packetName+":"+rawJson);
                tryToProcessPacket(packet, rawJson);
                return true;
            }catch (RuntimeException e){
                throw e;
            }catch (Exception t) {
                showError(t, "Could not process packet '" + packetName + "' from " + EnviromentPacketConfig.notEnviroment);
            }
        }
        String errorMessage = "Could not process packet '" + packetName + "' from " + EnviromentPacketConfig.notEnviroment;

        showError(new APIException("Found unknown packet: " + packetName + "'"), errorMessage);
        return false;
    }
}
