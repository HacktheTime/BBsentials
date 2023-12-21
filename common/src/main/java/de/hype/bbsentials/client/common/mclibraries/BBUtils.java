package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.shared.constants.Islands;

import java.util.List;

public interface BBUtils {
    Islands getCurrentIsland();

    int getPlayerCount();

    String getServer();

    boolean isOnMegaServer();

    boolean isOnMiniServer();

    int getMaximumPlayerCount();

    List<String> getPlayers();
}
