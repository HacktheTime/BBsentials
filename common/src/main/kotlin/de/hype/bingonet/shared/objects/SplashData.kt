package de.hype.bingonet.shared.objects

import de.hype.bingonet.shared.constants.Islands
import de.hype.bingonet.shared.constants.StatusConstants

open class SplashData @JvmOverloads constructor(
    @JvmField var announcer: String,
    @JvmField var locationInHub: SplashLocation,
    extraMessage: String?,
    @JvmField var lessWaste: Boolean,
    @JvmField var serverID: String?,
    /**
     * If null the Splash is in a private Mega → Request Invite.
     */
    @JvmField var hubSelectorData: HubSelectorData?,
    @JvmField var status: StatusConstants = StatusConstants.WAITING
) {
    @JvmField
    var splashId: Int = 0

    @JvmField
    var extraMessage: String?

    init {
        this.extraMessage = extraMessage?.replace("&", "§")
    }

    constructor(packet: SplashData) : this(
        packet.announcer,
        packet.locationInHub,
        packet.extraMessage,
        packet.lessWaste,
        packet.serverID,
        packet.hubSelectorData,
        packet.status
    ) {
        this.splashId = packet.splashId
    }

    fun update(data: SplashData) {
        this.announcer = data.announcer
        this.locationInHub = data.locationInHub
        this.extraMessage = data.extraMessage
        this.lessWaste = data.lessWaste
        this.serverID = data.serverID
        this.hubSelectorData = data.hubSelectorData
        this.status = data.status
        this.splashId = data.splashId
    }


    class HubSelectorData(hubNumber: Int, hubType: Islands) {
        @JvmField
        var hubNumber: Int

        @JvmField
        var hubType: Islands?

        init {
            require(hubType == Islands.HUB || hubType == Islands.DUNGEON_HUB) { "§cInvalid hub type specified. Please only use the Suggestions!" }
            require(!(hubNumber < 1 || hubNumber > 28)) { "§cInvalid hub number specified. Must be between 1 and 28" }
            this.hubNumber = hubNumber
            this.hubType = hubType
        }
    }
}