package de.hype.bingonet.environment.addonpacketconfig

import java.util.function.Consumer

class AddonPacket<T : AbstractAddonPacket>(val clazz: Class<T>, val consumer: Consumer<T>) {
    val name: String
        get() = clazz.getSimpleName()
}
