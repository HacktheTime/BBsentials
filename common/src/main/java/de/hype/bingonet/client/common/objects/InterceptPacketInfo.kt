package de.hype.bingonet.client.common.objects

import de.hype.bingonet.environment.packetconfig.AbstractPacket

internal fun interface PacketRunnable<T : AbstractPacket?> {
    fun run(packet: T)
}

abstract class InterceptPacketInfo<T : AbstractPacket?>(
    public val clazz: Class<T>,
    public val cancelPacket: Boolean,
    public val blockIntercepts: Boolean,
    public val ignoreIfIntercepted: Boolean,
    /**
     * block execution for completion is used whether the checks shall continue or the connection shall be paused until this is completed.
     */
    public val blockExecutionForCompletion: Boolean
) : PacketRunnable<T> {
    public val replyId: Long = -1

    fun matches(packetClass: Class<T>): Boolean {
        return packetClass == clazz
    }

}
