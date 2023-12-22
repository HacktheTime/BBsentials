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

    long getLobbyTime();

    default int getLobbyDay() {
        return (int) (getLobbyTime() / 24000);
    }

    List<String> getPlayers();
}
