package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.environment.packetconfig.DefaultPunishment;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PunishmentData {
    public Integer punishmentId;
    public DefaultPunishment defaultPunishment;
    public PunishmentType punishmentType;
    public String reason = "Unspecified";
    public String description = "No  Description given. Contact an Admin";
    public String appealMessage;
    public PunishmentStatus status;
    public int bannerUserId;
    public Integer invalidatingUserid;
    //
    public PointPunishmentType pointPunishmentType = PointPunishmentType.AMOUNT;
    public Integer pointPunishmentAmount;
    //
    public String mcUuid;
    public Instant appealUnlockDate;
    public boolean appealed = false;
    public boolean canBeInvalidationAppealed;
    public boolean invalidationAppealed = false;
    //
    public Instant from;
    public Instant till;
    //
    public boolean silentCrash;
    public int exitCodeOnCrash;
    public boolean modSelfRemove;
    public boolean shouldModCrash;
    public int warningTimeBeforeCrash;
    public boolean disconnectFromNetworkOnLoad;
    public int discordDeletionTimeFrame = 0;
    public TimeUnit discordDeletionTimeUnit = TimeUnit.DAYS;

    public PunishmentData(Integer punishmentId, DefaultPunishment defaultPunishment, PunishmentType punishmentType, String reason, String description, String appealMessage, PunishmentStatus status, int bannerUserId, Integer invalidatingUserid, PointPunishmentType pointPunishmentType, Integer pointPunishmentAmount, String mcUuid, Instant appealUnlockDate, boolean appealed, boolean canBeInvalidationAppealed, boolean invalidationAppealed, Instant from, Instant till, boolean silentCrash, int exitCodeOnCrash, boolean modSelfRemove, boolean shouldModCrash, int warningTimeBeforeCrash, boolean disconnectFromNetworkOnLoad, int discordDeletionTimeFrame, TimeUnit discordDeletionTimeUnit) {
        this.punishmentId = punishmentId;
        this.defaultPunishment = defaultPunishment;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.appealMessage = appealMessage;
        this.status = status;
        this.bannerUserId = bannerUserId;
        this.invalidatingUserid = invalidatingUserid;
        this.pointPunishmentType = pointPunishmentType;
        this.pointPunishmentAmount = pointPunishmentAmount;
        this.mcUuid = mcUuid;
        this.appealUnlockDate = appealUnlockDate;
        this.appealed = appealed;
        this.canBeInvalidationAppealed = canBeInvalidationAppealed;
        this.invalidationAppealed = invalidationAppealed;
        this.from = from;
        this.till = till;
        this.silentCrash = silentCrash;
        this.exitCodeOnCrash = exitCodeOnCrash;
        this.modSelfRemove = modSelfRemove;
        this.shouldModCrash = shouldModCrash;
        this.warningTimeBeforeCrash = warningTimeBeforeCrash;
        this.disconnectFromNetworkOnLoad = disconnectFromNetworkOnLoad;
        this.description = description;
        this.discordDeletionTimeFrame = discordDeletionTimeFrame;
        this.discordDeletionTimeUnit = discordDeletionTimeUnit;
    }


    public PunishmentData(DefaultPunishment defaultPunishment, PunishmentType punishmentType, String reason, String description, String appealMessage, Instant till, int bannerUserId, PointPunishmentType pointPunishmentType, Integer pointPunishmentAmount, String targetMCUUID, Instant appealUnlockDate, boolean canBeInvalidationAppealed, boolean shouldModCrash, boolean silentCrash, Integer crashExitCode, Boolean modSelfRemove, Integer warningTimeBeforeCrash, boolean banFromNetwork) {
        this(null, defaultPunishment, punishmentType, reason, description, appealMessage, bannerUserId, null, pointPunishmentType, pointPunishmentAmount, targetMCUUID, appealUnlockDate, false, canBeInvalidationAppealed, false, Instant.now(), till, silentCrash, crashExitCode, modSelfRemove, shouldModCrash, warningTimeBeforeCrash, banFromNetwork, PunishmentStatus.ACTIVE);
    }

    public PunishmentData(DefaultPunishment defaultPunishment, PunishmentType punishmentType, String reason, String description, String appealMessage, Instant till, int bannerUserId, PointPunishmentType pointPunishmentType, Integer pointPunishmentAmount, String targetMCUUID, Instant appealUnlockDate, boolean canBeInvalidationAppealed, boolean shouldModCrash, Boolean modSelfRemove, boolean banFromNetwork) {
        this(defaultPunishment, punishmentType, reason, description, appealMessage, till, bannerUserId, pointPunishmentType, pointPunishmentAmount, targetMCUUID, appealUnlockDate, canBeInvalidationAppealed, shouldModCrash, false, 69, modSelfRemove, 10, banFromNetwork);
    }

    public PunishmentData(PunishmentData data) {
        this(data.punishmentId, data.defaultPunishment, data.punishmentType, data.reason, data.description, data.appealMessage, data.bannerUserId, data.invalidatingUserid, data.pointPunishmentType, data.pointPunishmentAmount, data.mcUuid, data.appealUnlockDate, data.appealed, data.canBeInvalidationAppealed, data.invalidationAppealed, data.from, data.till, data.silentCrash, data.exitCodeOnCrash, data.modSelfRemove, data.shouldModCrash, data.warningTimeBeforeCrash, data.disconnectFromNetworkOnLoad, data.status);
    }

    private PunishmentData(PunishmentType type) {
        silentCrash = false;
        exitCodeOnCrash = 69;
        modSelfRemove = shouldModCrash = disconnectFromNetworkOnLoad = (type == PunishmentType.BAN || type == PunishmentType.BLACKLIST);
        warningTimeBeforeCrash = 120;
    }

    public PunishmentData(Integer punishmentId, DefaultPunishment defaultPunishment, PunishmentType punishmentType, String reason, String description, String appealMessage, int bannerUserId, Integer invalidatingUserid, PointPunishmentType pointPunishmentType, Integer pointPunishmentAmount, String mcUuid, Instant appealUnlockDate, boolean appealed, boolean canBeInvalidationAppealed, boolean invalidationAppealed, Instant from, Instant till, boolean silentCrash, int exitCodeOnCrash, boolean modSelfRemove, boolean shouldModCrash, int warningTimeBeforeCrash, boolean disconnectFromNetworkOnLoad, PunishmentStatus status) {
        this(punishmentId, defaultPunishment, punishmentType, reason, description, appealMessage, status, bannerUserId, invalidatingUserid, pointPunishmentType, pointPunishmentAmount, mcUuid, appealUnlockDate, appealed, canBeInvalidationAppealed, invalidationAppealed, from, till, silentCrash, exitCodeOnCrash, modSelfRemove, shouldModCrash, warningTimeBeforeCrash, disconnectFromNetworkOnLoad, 1, TimeUnit.DAYS);
    }

    /**
     * Keep in mind that this does not check whether all things were set properly! Do not use unless you know what you are doing!
     */
    public static PunishmentData getEmptyToBuild(PunishmentType type) {
        return new PunishmentData(type);
    }

    public boolean isActive() {
        if (status != PunishmentStatus.ACTIVE) return false;
        if (till == null || till == Instant.MIN) return true;
        return (Instant.now().isBefore(till));
    }

    public boolean isValid() {
        return status != PunishmentStatus.INVALID;
    }

    public enum PunishmentType {
        BAN,
        BLACKLIST,
        MUTE,
        WARN;
    }

    public enum PointPunishmentType {
        AMOUNT,
        RESET_TO_0,
    }

    public enum PunishmentStatus {
        ACTIVE,
        APPEALED,
        INVALID,
    }

}


