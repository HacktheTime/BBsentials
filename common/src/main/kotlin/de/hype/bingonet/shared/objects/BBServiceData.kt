package de.hype.bingonet.shared.objects

import de.hype.bingonet.server.objects.BBUser
import de.hype.bingonet.shared.constants.StatusConstants
import de.hype.bingonet.shared.constants.TradeType
import java.time.Instant

open class BBServiceData(
    @JvmField protected var type: TradeType,
    @JvmField protected var description: String,
    @JvmField protected var hoster: BBUser,
    @JvmField protected var price: Int,
    @JvmField protected var helpers: MutableList<Helper>,
    @JvmField protected var maxUsers: Int,
    @JvmField protected var forceModOnline: Boolean
) {
    var serviceId: Int = 0
        protected set

    @JvmField
    protected var status: StatusConstants = StatusConstants.OPEN

    @JvmField
    protected var title: String? = null

    @JvmField
    protected var participants: MutableList<Participant> = ArrayList<Participant>()

    @JvmField
    protected var queue: MutableList<Participant> = ArrayList<Participant>()
    protected lateinit var dcMessageID: String

    @JvmField
    protected var joinLock: Boolean = false

    @JvmField
    var circulateParticipants: Boolean = false

    constructor(
        type: TradeType,
        hoster: BBUser,
        price: Int,
        helpers: MutableList<Helper>,
        forceModOnline: Boolean
    ) : this(type, type.description, hoster, price, helpers, type.getMaximumUsers(helpers.size), forceModOnline)

    class Participant(
        @JvmField var user: BBUser,
        @JvmField var priority: Boolean,
        @JvmField var joinTime: Instant,
        @JvmField var autoRequeue: Boolean,
        @JvmField var price: Int
    ) {
        constructor(user: BBUser, priority: Boolean, free: Boolean, data: BBServiceData) : this(
            user,
            priority,
            free,
            Instant.now(),
            false,
            data
        )

        constructor(
            user: BBUser,
            priority: Boolean,
            free: Boolean,
            joinTime: Instant,
            autoRequeue: Boolean,
            data: BBServiceData
        ) : this(user, priority, joinTime, autoRequeue, if (free) 0 else data.price)

        override fun equals(obj: Any?): Boolean {
            if (obj is Participant) return obj.user == user
            if (obj is BBUser) return obj == this.user
            return false
        }

        override fun hashCode(): Int {
            return user.hashCode()
        }

        val discordParticipantString: String
            get() {
                var string = user.getMcUsername()
                if (price == 0) string = "**$string**"
                if (priority) string = "__${string}__"
                if (autoRequeue) string = "*$string*"
                return string
            }
    }

    class Helper {
        private var user: BBUser?
        private var username: String?

        constructor(user: BBUser) {
            this.user = user
            this.username = user.getMcUsername()
        }

        constructor(user: BBUser?, username: String) {
            this.user = user
            this.username = username
        }

        override fun equals(obj: Any?): Boolean {
            if (obj is Helper) return obj.username == username
            if (obj is String) {
                if (username != null) return obj.equals(username, ignoreCase = true)
                else return user!!.getMcUsername().equals(obj, ignoreCase = true)
            }
            if (obj is BBUser) {
                if (user != null) return obj == user
                else return obj.getMcUsername().equals(username, ignoreCase = true)
            }
            return false
        }

        override fun hashCode(): Int {
            if (username != null) return username.hashCode()
            else return user!!.getMcUsername().hashCode()
        }

        fun getUserName(): String {
            return username ?: user!!.mcUsername
        }

        fun getUser(): BBUser? {
            return user
        }
    }
}
