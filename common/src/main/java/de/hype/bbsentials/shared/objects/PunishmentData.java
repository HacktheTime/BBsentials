package de.hype.bbsentials.shared.objects;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PunishmentData {
    public Integer punishmentId;
    public Integer punishedUserId;
    public Type type;
    public String mcUuid;
    public Date from;
    public Date till;
    public String reason;
    public Boolean silent;
    public Integer bannerUserId;
    public Boolean modSelfRemove;
    public Boolean shouldModCrash;
    public Integer warningTimeBeforeCrash;
    public Boolean disconnectFromNetworkOnLoad;
    public Integer exitCodeOnCrash;
    public Boolean canBeAppealed;
    public Integer invalidatingUserid = -1;

    public PunishmentData(Integer punishmentId, Integer punishedUserId, Type type, String mcUuid, Date from, Date till, String reason, Boolean silent, Integer bannerUserId, Boolean modSelfRemove, Boolean shouldModCrash, Integer warningTimeBeforeCrash, Boolean disconnectFromNetworkOnLoad, Integer exitCodeOnCrash, Boolean canBeAppealed) {
        this.punishmentId = punishmentId;
        this.punishedUserId = punishedUserId;
        this.type = type;
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
        this.canBeAppealed = canBeAppealed;
    }

    public enum Type {
        BAN,
        BLACKLIST,
        MUTE,
    }

    public static Date getTillDateFromDurationString(String duration) throws Exception {
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        Pattern durationPattern = Pattern.compile("(\\d+)([dhms])");
        Matcher durationMatcher = durationPattern.matcher(duration);

        while (durationMatcher.find()) {
            int amount = Integer.parseInt(durationMatcher.group(1));
            String unit = durationMatcher.group(2);

            switch (unit) {
                case "d":
                    days = amount;
                    break;
                case "h":
                    hours = amount;
                    break;
                case "m":
                    minutes = amount;
                    break;
                case "s":
                    seconds = amount;
                    break;
                default:
                    throw new Exception("§cInvalid Duration Parameter: " + unit);
            }

        }

        // Calculate the ban duration in milliseconds
        long durationMillis = (((days * 24L + hours) * 60 + minutes) * 60 + seconds) * 1000;
        Date from = new java.sql.Date(new Date().getTime());
        Date till = new java.sql.Date(from.getTime() + durationMillis);
        if (durationMillis == 0) {
            till = new Date(0L);
        }
        return till;
    }

    public boolean isActive() {
        if (invalidatingUserid != -1) return false;
        Date currentDate = new Date();
        return (till != null && currentDate.before(till));
    }
}
