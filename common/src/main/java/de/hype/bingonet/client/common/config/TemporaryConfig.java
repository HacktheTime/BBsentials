package de.hype.bingonet.client.common.config;

import de.hype.bingonet.client.common.objects.ChatPrompt;
import de.hype.bingonet.client.common.objects.WaypointRoute;
import de.hype.bingonet.shared.constants.ChChestItem;
import de.hype.bingonet.shared.objects.Position;

import java.time.Instant;
import java.util.*;


public class TemporaryConfig extends BingoNetConfig {
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;
    public transient WaypointRoute route = null;
    public transient Map<String, Integer> serverIdToHubNumber = new HashMap<>();
    public transient Instant lastServerIdUpdateDate = Instant.now();
    public Position lastGlobalChchestCoords;

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
