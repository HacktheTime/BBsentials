package de.hype.bingonet.environment.addonpacketconfig

import com.google.gson.Gson
import de.hype.bingonet.client.common.chat.Chat
import de.hype.bingonet.client.common.client.BingoNet
import de.hype.bingonet.client.common.client.CustomGson
import de.hype.bingonet.client.common.client.socketAddons.AddonHandler

object AddonPacketUtils {
    val gson: Gson = CustomGson.createNotPrettyPrinting()

    fun parsePacketToJson(packet: AbstractAddonPacket?): String {
        return gson.toJson(packet).replace("\n", "/n")
    }

    fun <T : AbstractAddonPacket> tryToProcessPacket(addonPacket: AddonPacket<T>, rawJson: String) {
        val clazz = addonPacket.clazz
        val consumer = addonPacket.consumer
        val abstractPacket = gson.fromJson(rawJson.replace("/n", "\n"), clazz)
        consumer.accept(abstractPacket)
    }

    private fun showError(t: Throwable, errorMessage: String?) {
        println(errorMessage + " because of: " + t.javaClass.getSimpleName() + ":  " + t.message)
        Error(errorMessage, t).printStackTrace()
    }

    fun <T : AbstractAddonPacket?> getAsPacket(message: String, clazz: Class<T?>): T? {
        if (!message.contains(".")) return null
        val packetName = message.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val rawJson = message.substring(packetName.length + 1)
        if (packetName != clazz.getSimpleName()) {
            try {
                val parsedPacket = gson.fromJson<T?>(rawJson.replace("/n", "\n"), clazz)
                return parsedPacket
            } catch (t: Throwable) {
                showError(
                    t,
                    "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment
                )
            }
        }
        val errorMessage =
            "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment

        showError(APIException("Found unknown Addon packet: " + packetName + "'"), errorMessage)
        return null
    }

    fun isPacket(message: String, clazz: Class<out AbstractAddonPacket?>): Boolean {
        if (!message.contains(".")) return false
        val packetName = message.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        if (packetName == clazz.getSimpleName()) {
            return true
        }
        return false
    }

    fun isPacket(message: String): Boolean {
        if (!message.contains(".")) return false
        val packetName = message.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        for (packetClass in AddonPacketManager.Companion.allPacketClasses) {
            if (packetName != packetClass.getSimpleName()) {
                return true
            }
        }
        return false
    }

    fun <T : AbstractAddonPacket?> handleIfPacket(connection: AddonHandler?, message: String): Boolean {
        //Return = is Packet
        if (!message.contains(".")) return false
        val packetName = message.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val rawJson = message.substring(packetName.length + 1)
        val manager = AddonPacketManager(connection)
        for (addonPacket in manager.packets) {
            if (packetName != addonPacket.clazz.simpleName) continue
            try {
                if (BingoNet.socketAddonConfig.addonDebug) Chat.sendPrivateMessageToSelfDebug(packetName + ":" + rawJson)
                AddonPacketUtils.tryToProcessPacket(addonPacket, rawJson)
                return true
            } catch (t: Throwable) {
                showError(
                    t,
                    "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment
                )
            }
        }
        val errorMessage =
            "Could not process Addon packet '" + packetName + "' from " + AddonEnvironmentPacketConfig.notEnviroment

        showError(APIException("Found unknown Addon packet: $packetName'"), errorMessage)
        return false
    }

    class APIException : Error {
        constructor(errorMessage: String, t: Throwable) : super(errorMessage, t)

        constructor(errorMessage: String) : super(errorMessage)
    }
}
