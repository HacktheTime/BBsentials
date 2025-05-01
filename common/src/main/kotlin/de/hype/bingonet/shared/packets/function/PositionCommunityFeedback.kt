package de.hype.bingonet.shared.packets.function

import de.hype.bingonet.environment.packetconfig.AbstractPacket
import java.util.*

class PositionCommunityFeedback(@JvmField var positions: MutableSet<ComGoalPosition>) : AbstractPacket(1, 1) {
    class ComGoalPosition(
        @JvmField var goalName: String,
        @JvmField var contribution: Int,
        @JvmField var topPercentage: Double,
        @JvmField var position: Int?
    ) {
        override fun hashCode(): Int {
            return goalName.hashCode()
        }

        fun dataEquals(compare: ComGoalPosition?): Boolean {
            if (compare == null) return false
            return (Objects.equals(
                topPercentage,
                compare.topPercentage
            )) && compare.position == this.position && contribution == compare.contribution
        }
    }
}
