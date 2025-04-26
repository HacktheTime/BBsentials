package de.hype.bingonet.environment.addonpacketconfig

import de.hype.bingonet.client.common.client.socketAddons.AddonHandler
import de.hype.bingonet.shared.packets.addonpacket.*

class AddonPacketManager(// Define a map to store packet classes and their associated actions
    var connection: AddonHandler?
) {
    var packets: MutableList<AddonPacket<out AbstractAddonPacket>> =
        ArrayList()

    // Method to initialize packet actions
    init {
        initializePacketActions(connection)
        lastAddonPacketManager = this
    }

    fun initializePacketActions(connection: AddonHandler?) {
        packets.add(
            AddonPacket(
                DisplayTellrawMessageAddonPacket::class.java
            ) { packet: DisplayTellrawMessageAddonPacket ->
                connection?.onDisplayTellrawMessageAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                DisplayClientsideMessageAddonPacket::class.java
            ) { packet: DisplayClientsideMessageAddonPacket ->
                connection?.onDisplayClientsideMessageAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                PlaySoundAddonPacket::class.java
            ) { packet: PlaySoundAddonPacket ->
                connection?.onPlaySoundAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                PublicChatAddonPacket::class.java
            ) { packet: PublicChatAddonPacket ->
                connection?.onPublicChatAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                ServerCommandAddonPacket::class.java
            ) { packet: ServerCommandAddonPacket ->
                connection?.onServerCommandAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                ChatPromptAddonPacket::class.java
            ) { packet: ChatPromptAddonPacket ->
                connection?.onChatPromptAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                WaypointAddonPacket::class.java
            ) { packet: WaypointAddonPacket ->
                connection?.onWaypointAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                GetWaypointsAddonPacket::class.java
            ) { packet: GetWaypointsAddonPacket -> connection?.onGetWaypointsAddonPacket(packet) }
        )
        packets.add(
            AddonPacket(
                ClientCommandAddonPacket::class.java
            ) { packet: ClientCommandAddonPacket ->
                connection?.onClientCommandAddonPacket(packet)
            }
        )
        packets.add(
            AddonPacket(
                GoToIslandAddonPacket::class.java
            ) { packet: GoToIslandAddonPacket ->
                connection?.onGoToIslandAddonPacket(packet)
            }
        )
        //        addonPackets.add(new AddonPacket<>(ReceivedPublicChatMessageAddonPacket.class, connection::onReceivedPublicChatMessageAddonPacket));
        packets.add(
            AddonPacket(
                StatusUpdateAddonPacket::class.java
            ) { packet: StatusUpdateAddonPacket -> connection?.onStatusUpdateAddonPacket(packet) }
        )
    }


    companion object {
        private var lastAddonPacketManager: AddonPacketManager? = null
        val allPacketClasses: MutableList<Class<out AbstractAddonPacket>>
            // Method to handle a received packet
            get() {
                if (lastAddonPacketManager == null) {
                    lastAddonPacketManager = AddonPacketManager(null)
                }
                val allPackets: MutableList<Class<out AbstractAddonPacket>> =
                    ArrayList()
                for (i in lastAddonPacketManager!!.packets.indices) {
                    allPackets.add(lastAddonPacketManager!!.packets.get(i).clazz)
                }
                return allPackets
            }
    }
}
