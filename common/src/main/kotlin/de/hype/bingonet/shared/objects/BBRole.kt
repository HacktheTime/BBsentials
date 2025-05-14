package de.hype.bingonet.shared.objects

enum class BBRole(dbRoleName: String, visualRoleName: String) {
    DEVELOPER("dev", "Developer"),
    MODERATOR("mod", "Moderator"),
    CHCHEST_ANNOUNCE_PERM("chchest", "Ch Chest"),
    BETA_TESTER("beta", "Beta Tester"),
    ADMIN("admin", "Admin"),
    MANIAC("maniac", "Maniac"),
    MINING_EVENT_ANNOUNCE_PERM("mining_events", "Mining Events"),
    PREANNOUNCE("preannounce_info", "Preannounce Info"),
    SPLASHER("splasher", "Splasher"),
    ADVANCEDINFO("advancedinfo", "Advanced Info"),
    STRATMAKER("strat_maker", "Strat Maker"),
    DEBUG("debug", "Debug");

    val dBRoleName: String
    val visualName: String


    init {
        this.dBRoleName = dbRoleName
        this.visualName = visualRoleName
    }

    val description: String?
        get() = null

    companion object {
        var roles: MutableMap<String, BBRole>? = null

        @JvmStatic
        fun getRoleByDBName(dbRoleName: String): BBRole {
            roles?.let { return it[dbRoleName]!! }
            val roles = HashMap<String, BBRole>()
            for (value in entries) {
                roles.put(value.dBRoleName, value)
            }
            this.roles = roles
            return roles.get(dbRoleName)!!
        }
    }
}
