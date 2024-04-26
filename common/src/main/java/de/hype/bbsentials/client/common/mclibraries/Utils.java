package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.Position;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import net.hypixel.modapi.packet.HypixelPacket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    default void playCustomSound(String path, int duration) {
        try {
            AudioInputStream audioInputStream;
            InputStream inputStream = getClass().getResourceAsStream(path);
            audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioInputStream.close();
                    } catch (IOException e) {
                        Chat.sendPrivateMessageToSelfError("Error Closing Sound Stream: " + e.getMessage());
                    }
                }
            });

            clip.open(audioInputStream);
            if (duration != 0) BBsentials.executionService.schedule(() -> {
                try {
                    clip.stop();
                    clip.close();
                    audioInputStream.close();
                } catch (IOException e) {
                    Chat.sendPrivateMessageToSelfError("Error Closing Sound Stream: " + e.getMessage());
                }
            }, duration, TimeUnit.SECONDS);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default void streamCustomSound(String pathOrUrl, int durationinSeconds) {
        try {
            URL soundUri = new URI(pathOrUrl).toURL();
            InputStream inputStream = soundUri.openStream();
            AdvancedPlayer player = new AdvancedPlayer(inputStream);

            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    try {
                        player.close();
                        inputStream.close();
                    } catch (Exception e) {
                    }
                }
            });
            if (durationinSeconds != 0) {
                BBsentials.executionService.schedule(() -> {
                    try {
                        player.stop();
                        player.close();
                        inputStream.close();
                    } catch (IOException e) {
                        Chat.sendPrivateMessageToSelfError("Error Closing Sound Stream: " + e.getMessage());
                    }
                }, durationinSeconds, TimeUnit.SECONDS);
            }
            player.play();

        } catch (Exception e) {

        }
    }


    boolean isInGame();

    void showErrorScreen(String s);

    default void shutdownPC() throws IOException {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("shutdown", "/s", "/t", String.valueOf(20), "/c", "The System is shutting down forcefully in 20 Seconds. Make sure to save everything.");
        }
        else {
            processBuilder = new ProcessBuilder("systemctl", "poweroff");
        }
        processBuilder.start();
    }

    default void hibernatePC() throws IOException {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("shutdown", "/s", "/hybrid", "/t", String.valueOf(20), "/c", "The System is going into hibernation in 20 Seconds. Make sure to save everything.");
        }
        else {
            processBuilder = new ProcessBuilder("systemctl", "hibernate");
        }
        processBuilder.start();
    }

    default void suspendPC() throws IOException {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("shutdown", "/h");
        }
        else {
            processBuilder = new ProcessBuilder("systemctl", "suspend");
        }
        processBuilder.start();
    }

    boolean isSelfBingo();

    String getServerConnectedAddress();

    void registerNetworkHandlers();

    void sendPacket(HypixelPacket packet);

    void sendPacket(String identifier);

    UUID getMCUUIDID();
}
