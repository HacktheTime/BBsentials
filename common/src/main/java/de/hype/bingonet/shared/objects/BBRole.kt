package de.hype.bingonet.shared.objects;

import java.util.HashMap;
import java.util.Map;


public enum BBRole {
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
    public static Map<String, BBRole> roles;
    public final String dbRoleName;
    public final String visulaRoleName;


    BBRole(String dbRoleName, String visulaRoleName) {
        this.dbRoleName = dbRoleName;
        this.visulaRoleName = visulaRoleName;
    }

    public static BBRole getRoleByDBName(String dbRoleName) {
        if (roles != null) return roles.get(dbRoleName);
        roles = new HashMap<>();
        for (BBRole value : BBRole.values()) {
            roles.put(value.dbRoleName, value);
        }
        return roles.get(dbRoleName);
    }

    public String getDBRoleName() {
        return dbRoleName;
    }

    public String getVisualName() {
        return visulaRoleName;
    }

    public String getDescription() {
        return null;
    }
}
