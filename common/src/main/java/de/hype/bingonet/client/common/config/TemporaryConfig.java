package de.hype.bingonet.client.common.config;

import de.hype.bingonet.client.common.objects.ChatPrompt;
import de.hype.bingonet.client.common.objects.WaypointRoute;
import de.hype.bingonet.shared.constants.Islands;
import de.hype.bingonet.shared.objects.Position;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemporaryConfig extends BingoNetConfig {
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;
    public transient WaypointRoute route = null;
    public transient Map<Islands, Map<String, Integer>> serverIdToHubNumber = new HashMap<>();
    public transient Instant lastServerIdUpdateDate = Instant.now();
    public Position lastGlobalChchestCoords;

    public TemporaryConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }

    public Integer getHubNumberFromCache(Islands islandType, String serverId) {
        if (!lastServerIdUpdateDate.isAfter(Instant.now().minusSeconds(60))) return null;
        Map<String, Integer> serverIdToHubNumber = this.serverIdToHubNumber.get(islandType);
        if (serverIdToHubNumber == null) return null;
        return serverIdToHubNumber.get(serverId);
    }
}
