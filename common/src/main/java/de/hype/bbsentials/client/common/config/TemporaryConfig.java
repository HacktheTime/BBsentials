package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.WaypointRoute;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemporaryConfig extends BBsentialsConfig {
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;
    public transient WaypointRoute route = null;
    public transient Map<String, Integer> serverIdToHubNumber = new HashMap<>();
    public transient Instant lastServerIdUpdateDate = Instant.now();

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
