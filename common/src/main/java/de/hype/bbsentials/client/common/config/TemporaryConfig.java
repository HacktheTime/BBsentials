package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.WaypointRoute;
import de.hype.bbsentials.shared.constants.ChChestItem;

import java.time.Instant;
import java.util.*;


public class TemporaryConfig extends BBsentialsConfig {
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;
    public transient WaypointRoute route = null;
    public transient Map<String, Integer> serverIdToHubNumber = new HashMap<>();
    public transient Instant lastServerIdUpdateDate = Instant.now();
    public transient Set<ChChestItem> chestParts = new HashSet<>();
    public transient Long playTimeInMinutes;
    public transient Instant lastPlaytimeUpdate;

    public TemporaryConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }

    public Integer getHubNumberFromCache(String serverId) {
        if (!lastServerIdUpdateDate.isAfter(Instant.now().minusSeconds(60))) return null;
        return serverIdToHubNumber.get(serverId);
    }
}
