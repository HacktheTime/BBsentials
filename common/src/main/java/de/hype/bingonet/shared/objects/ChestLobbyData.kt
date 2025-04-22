package de.hype.bingonet.shared.objects

import de.hype.bingonet.shared.constants.StatusConstants
import java.awt.Color
import java.sql.SQLException
import java.time.Instant

class ChestLobbyData(
    chest: MutableCollection<ChChestData>,
    serverId: String,
    bbcommand: String,
    extraMessage: String?,
    status: Any?
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

    fun getStatus(): String {
        return status
    }

    /**
     * @param statusBase String or StatusConstants as buttonStyle.
     * @throws IllegalArgumentException if Object is not a [String] or [StatusConstants]
     */
    @Throws(SQLException::class)
    fun setStatus(statusBase: Any?) {
        setStatusNoOverride(statusBase)
    }

    @Throws(SQLException::class)
    fun setStatus(statusBase: StatusConstants?) {
        setStatusNoOverride(statusBase)
    }

    fun setStatusNoOverride(statusBase: Any?) {
        if (statusBase is StatusConstants) {
            this.status = statusBase.displayName
            color = statusBase.color
        } else if (statusBase is String) {
            this.status = statusBase
        } else {
            throw IllegalArgumentException("Invalid input buttonStyle. Expected String or StatusConstants.")
        }
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

    fun getPlayersStillIn(): MutableList<String>? {
        return playersStillIn
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is ChestLobbyData) return false
        return obj.lobbyId == lobbyId
    }

    protected fun updateLobby(lobby: ChestLobbyData) {
        bbcommand = lobby.bbcommand
        extraMessage = lobby.extraMessage
        status = lobby.getStatus()
        contactMan = lobby.contactMan
        chests = ArrayList<ChChestData>(lobby.chests)
        try {
            setLobbyMetaData(lobby.getPlayersStillIn(), lobby.closingTime)
        } catch (ignored: SQLException) {
        }
    }

    override fun hashCode(): Int {
        return lobbyId
    }
}
