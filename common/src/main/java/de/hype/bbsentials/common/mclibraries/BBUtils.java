package de.hype.bbsentials.common.mclibraries;

import de.hype.bbsentials.common.constants.enviromentShared.Islands;

import java.util.List;

public abstract class BBUtils {
    public abstract Islands getCurrentIsland();

    public abstract int getPlayerCount();

    public abstract String getServer();

    public abstract boolean isOnMegaServer();

    public abstract boolean isOnMiniServer();

    public abstract int getMaximumPlayerCount();

    public abstract List<String> getPlayers();
}
