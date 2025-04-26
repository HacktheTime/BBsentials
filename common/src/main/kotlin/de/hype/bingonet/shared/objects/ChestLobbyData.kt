package de.hype.bingonet.shared.objects

import de.hype.bingonet.shared.constants.StatusConstants
import java.awt.Color
import java.sql.SQLException
import java.time.Instant

class ChestLobbyData(
    chest: List<ChChestData>,
    serverId: String,
    bbcommand: String,
    extraMessage: String?,
    status: String
) {
    @JvmField
    var contactMan: String

    @JvmField
    var chests: MutableList<ChChestData> = ArrayList()

    @JvmField
    var bbcommand: String

    @JvmField
    var extraMessage: String?

    @Transient
    lateinit var color: Color

    @JvmField
    var lobbyId: Int = 0

    @JvmField
    var serverId: String
    lateinit var status: String
    var playersStillIn: MutableList<String>? = ArrayList()
    var closingTime: Instant? = null
        protected set

    init {
        chests.addAll(chest)
        this.serverId = serverId
        this.contactMan = chests.first().finder
        this.bbcommand = bbcommand
        this.extraMessage = extraMessage
        setStatusNoOverride(status)
    }

    constructor(
        chest: List<ChChestData>,
        serverId: String,
        bbcommand: String,
        extraMessage: String?,
        status: StatusConstants
    ) : this(
        chest,
        serverId,
        bbcommand,
        extraMessage,
        status.displayName
    ) {
        this.color = status.color
    }

    @Throws(SQLException::class)
    fun setStatus(statusBase: StatusConstants) {
        setStatusNoOverride(statusBase)
    }

    fun setStatusNoOverride(statusBase: String) {
        this.status = statusBase
    }

    fun setStatusNoOverride(statusBase: StatusConstants) {
        this.status = statusBase.displayName
        color = statusBase.color

    }

    fun addChest(chest: ChChestData) {
        chests.add(chest)
    }

    fun transferToUser(newContactMan: String) {
        contactMan = newContactMan
    }

    @Throws(SQLException::class)
    fun setLobbyMetaData(playersStillIn: MutableList<String>?, closingTime: Instant?) {
        this.playersStillIn = playersStillIn
        if (closingTime != null) {
            this.closingTime = closingTime
            onLobbyUpdate()
        }
    }

    fun onLobbyUpdate() {
        // need to be overridden
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is ChestLobbyData) return false
        return obj.lobbyId == lobbyId
    }

    protected fun updateLobby(lobby: ChestLobbyData) {
        bbcommand = lobby.bbcommand
        extraMessage = lobby.extraMessage
        status = lobby.status
        contactMan = lobby.contactMan
        chests = ArrayList<ChChestData>(lobby.chests)
        try {
            setLobbyMetaData(lobby.playersStillIn, lobby.closingTime)
        } catch (ignored: SQLException) {
        }
    }

    override fun hashCode(): Int {
        return lobbyId
    }
}
