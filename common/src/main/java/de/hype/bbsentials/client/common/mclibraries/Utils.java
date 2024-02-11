package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.Position;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface Utils {
    Islands getCurrentIsland();

    int getPlayerCount();

    String getServerId();

    boolean isOnMegaServer();

    boolean isOnMiniServer();

    int getMaximumPlayerCount();

    long getLobbyTime();

    default int getLobbyDay() {
        return (int) (getLobbyTime() / 24000);
    }

    List<String> getPlayers();

    boolean isWindowFocused();

    File getConfigPath();

    String getUsername();

    String getMCUUID();

    default void playsound(String eventName, String namespace) {
        playsound(namespace + ":" + eventName);
    }

    void playsound(String fullName);


    int getPotTime();

    String mojangAuth(String serverId);

    // Leechers was originally inveneted by Calva but redone by me without access to the code, I made it since Calvas mod was private at that date
    List<String> getSplashLeechingPlayers();

    InputStream makeScreenshot();

    String getStringFromTextJson(String textJson) throws Exception;

    boolean executeClientCommand(String command);

    boolean isJsonParseableToText(String json);

    String stringToTextJson(String string);
    Position getPlayersPosition();

    void systemExit(int id);

}
