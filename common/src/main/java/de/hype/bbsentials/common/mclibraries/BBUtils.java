package de.hype.bbsentials.common.mclibraries;

import de.hype.bbsentials.common.constants.enviromentShared.Islands;

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
