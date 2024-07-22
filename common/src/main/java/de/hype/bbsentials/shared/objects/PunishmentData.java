package de.hype.bbsentials.shared.objects;
import de.hype.bbsentials.environment.packetconfig.DefaultPunishment;

import java.time.Instant;

public class PunishmentData {
    public Integer punishmentId;
    public Integer punishedUserId;
    public DefaultPunishment defaultPunishment;
    public Type type;
    /**
     * -1 resets to 0;
     * null/0 is none
     */
    public Integer pointPunishment;
    public String mcUuid;
    public Instant from;
    public Instant till;
    public Instant appealUnlockDate;
    public boolean appealAccepted;
    public String reason;
    public boolean silent;
    public int bannerUserId;
    public boolean modSelfRemove;
    public boolean shouldModCrash;
    public int warningTimeBeforeCrash;
    public boolean disconnectFromNetworkOnLoad;
    public int exitCodeOnCrash;
    public boolean canBeInvalidationAppealed;
    public boolean invalidationAppealed;
    public int invalidatingUserid;

    public PunishmentData(PunishmentData data) {
        this(data.punishmentId, data.punishedUserId, data.type, data.pointPunishment, data.mcUuid, data.from, data.till, data.appealAccepted, data.reason, data.silent, data.bannerUserId, data.modSelfRemove, data.shouldModCrash, data.warningTimeBeforeCrash, data.disconnectFromNetworkOnLoad, data.exitCodeOnCrash, data.appealUnlockDate, data.canBeInvalidationAppealed, data.defaultPunishment,-1, data.invalidationAppealed);
    }

    public PunishmentData(Integer punishmentId, Integer punishedUserId, Type type, Integer pointPunishment, String mcUuid, Instant till, String reason, Boolean silent, Integer bannerUserId, Boolean modSelfRemove, Boolean shouldModCrash, Integer warningTimeBeforeCrash, Boolean disconnectFromNetworkOnLoad, Integer exitCodeOnCrash, Instant appealUnlockDate, boolean canBeInvalidationAppealed, DefaultPunishment defaultPunishment) {
        this(punishmentId, punishedUserId, type, pointPunishment, mcUuid, Instant.now(), till, false, reason, silent, bannerUserId, modSelfRemove, shouldModCrash, warningTimeBeforeCrash, disconnectFromNetworkOnLoad, exitCodeOnCrash, appealUnlockDate, canBeInvalidationAppealed, defaultPunishment, -1,false);
    }

    public PunishmentData(Integer punishmentId, Integer punishedUserId, Type type, Integer pointPunishment, String mcUuid, Instant from, Instant till, boolean appealAccepted, String reason, Boolean silent, Integer bannerUserId, Boolean modSelfRemove, Boolean shouldModCrash, Integer warningTimeBeforeCrash, Boolean disconnectFromNetworkOnLoad, Integer exitCodeOnCrash, Instant appealUnlockDate, boolean canBeInvalidationAppealed, DefaultPunishment defaultPunishment, int invalidatingUserid, boolean invalidationAppealed) {
        this.punishmentId = punishmentId;
        this.punishedUserId = punishedUserId;
        this.type = type;
        this.appealAccepted = appealAccepted;
        this.defaultPunishment = defaultPunishment;
        this.pointPunishment = pointPunishment;
        this.mcUuid = mcUuid;
        this.from = from;
        this.till = till;
        this.reason = reason;
        this.silent = silent;
        this.bannerUserId = bannerUserId;
        this.modSelfRemove = modSelfRemove;
        this.shouldModCrash = shouldModCrash;
        this.warningTimeBeforeCrash = warningTimeBeforeCrash;
        this.disconnectFromNetworkOnLoad = disconnectFromNetworkOnLoad;
        this.exitCodeOnCrash = exitCodeOnCrash;
        this.canBeInvalidationAppealed = canBeInvalidationAppealed;
        this.invalidatingUserid=invalidatingUserid;
        this.invalidationAppealed = invalidationAppealed;
        this.appealUnlockDate = appealUnlockDate;
    }

    public boolean isActive() {
        if (!isValid()) return false;
        if (appealAccepted) return false;
        return (till != null && Instant.now().isBefore(till));
    }

    public boolean isValid() {
        return invalidatingUserid == -1;
    }

    public enum Type {
        BAN(true, 69, true, true, false, true, 10),
        BLACKLIST(true, 69, true, true, false, true, 10),
        MUTE(false, 69, false, false, false, true, 10),
        WARN(false, 69, false, false, false, true, 10);

        public final boolean disconnect;
        public final int crashExitCode;
        public final boolean crash;
        public final boolean selfRemove;
        public final boolean silent;
        public final boolean appealable;
        public final int warnTime;

        Type(boolean disconnect, int crashExitCode, boolean crash, boolean selfRemove, boolean silent, boolean appealable, int warnTime) {
            this.disconnect = disconnect;
            this.crashExitCode = crashExitCode;
            this.crash = crash;
            this.selfRemove = selfRemove;
            this.silent = silent;
            this.appealable = appealable;
            this.warnTime = warnTime;
        }
    }
}
