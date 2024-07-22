package de.hype.bbsentials.environment.packetconfig;

import de.hype.bbsentials.shared.objects.PunishmentData;

import java.time.Instant;

public enum DefaultPunishment {
    BOT_SPAM("Bot Spam", "botspam"),
    SPLASH_LEECH("Splash Leech", "splashleech"),

    CHAT_LOW("Chat Low", "chatlow"),
    CHAT_MEDIUM("Chat Medium", "chatmedium"),
    CHAT_HARD("Chat Hard", "chathard"),

    CHEATING("Cheating", "cheating"),
    RATTING("Ratting", "ratting"),
    SCAMMING("Scamming", "scamming"),

    CUSTOM("Custom", "custom"),
    ;

    public final String name;
    public final String idsuffix;

    DefaultPunishment(String name, String idsuffix) {
        this.name = name;
        this.idsuffix = idsuffix;
    }
}
