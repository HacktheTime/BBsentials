package de.hype.bingonet.client.common.discordintegration;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.client.common.objects.InterceptPacketInfo;
import de.hype.bingonet.shared.constants.Islands;
import de.hype.bingonet.shared.packets.network.RequestUserInfoPacket;
import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import de.jcm.discordgamesdk.lobby.*;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GameSDKManager extends DiscordEventAdapter {
    private final String mcUsername = BingoNet.generalConfig.getUsername();
    private AtomicBoolean stop = new AtomicBoolean(false);
    private Core core;
    private ScheduledFuture callbackRunner;

    public GameSDKManager() throws Exception {
        // Initialize the Core
        if (!initCore()) throw new Exception("Could not initialise the discord game sdk");
        connectToDiscord();
        callbackRunner = BingoNet.executionService.scheduleAtFixedRate(() -> {
            try {
                runContinously();
            } catch (Exception ignored) {
            }
        }, 0, 14, TimeUnit.MILLISECONDS);
        BingoNet.executionService.scheduleAtFixedRate(this::updateActivity, 1, 20, TimeUnit.SECONDS);
    }

    public static boolean initCore() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        File sdkDir = new File(EnvironmentCore.utils.getConfigPath() + "/discord_game_sdk");
        sdkDir.mkdirs();
//        Core.initDownload();
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        }
        else if (osName.contains("linux")) {
            suffix = ".so";
        }
        else if (osName.contains("mac os")) {
            suffix = ".dylib";
        }
        else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }
        File prefile = new File(sdkDir, name + suffix);
        if (prefile.exists()) {
            try {
                Core.init(prefile);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
        if (arch.equals("amd64"))
            arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/" + arch + "/" + name + suffix;

        // Open the URL as a ZipInputStream
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
        ZipInputStream zin = new ZipInputStream(downloadUrl.openStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                Files.copy(zin, prefile.toPath());
                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                try {
                    Core.init(prefile);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return false;
    }

    public void stop() {
        stop.set(true);
    }

    public void runContinously() {
        try {
            core.runCallbacks();
        } catch (Exception e) {

        }
    }

    public void updateActivity() {
        // Create the Activity
        try (Activity activity = new Activity()) {
            if (BingoNet.developerConfig.devMode) {
                activity.setDetails("Programming this ↑");
                activity.setState("Developer Mode: Enabled");
                activity.assets().setLargeImage("i_am_root_backup_laugh");
                activity.assets().setLargeText("I am Root (→ Linux for I'm the Admin)");
                activity.timestamps().setStart(Instant.now());
                activity.assets().setSmallText("BingoNet. A mod by @hackthetime");
                activity.assets().setSmallImage("bingo_hub");
            }
            else {
                Islands island = EnvironmentCore.utils.getCurrentIsland();
                if (island != null) {
                    activity.setDetails("Playing Hypixel Skyblock");
                    activity.setState(EnvironmentCore.utils.getServerId() + ": " + EnvironmentCore.utils.getCurrentIsland().getDisplayName());
                    activity.assets().setSmallText("BingoNet. A mod by @hackthetime");
                    activity.assets().setSmallImage("bingo_hub");
                    activity.assets().setLargeImage("bingo_card");
                }
                else {
                    activity.setDetails("Playing Minecraft");
                    activity.setState("");
                    activity.assets().setLargeText("BingoNet. A mod by @hackthetime");
                    activity.assets().setLargeImage("bingo_hub");
                }
            }
            // Setting a start time causes an "elapsed" field to appear
            activity.timestamps().setStart(Instant.ofEpochSecond(0));

            // We are in a party with 10 out of 100 people.
            try {
                int maxPlayers = EnvironmentCore.utils.getMaximumPlayerCount();
                int currentPlayers = EnvironmentCore.utils.getPlayerCount();
                activity.party().size().setMaxSize(maxPlayers);
                activity.party().size().setCurrentSize(currentPlayers);
            } catch (Exception e) {

            }

//            activity.party().size().setMaxSize(EnvironmentCore.utils.getMaximumPlayerCount());
//            activity.party().size().setCurrentSize(EnvironmentCore.utils.getPlayerCount());

            // Make a "cool" image show up

            // Setting a join secret and a party ID causes an "Ask to Join" button to appear
//            if (currentLobby == null) blockingCreateDefaultLobby();
//            activity.party().setID(String.valueOf(currentLobby.getId()));
//            if (BingoNet.discordConfig.useRPCJoin)
//                activity.secrets().setJoinSecret(getLobbyManager().getLobbyActivitySecret(currentLobby));
//            if (BingoNet.discordConfig.useRPCSpectate) {
////                activity.secrets().setSpectateSecret(getLobbyManager().getLobbyActivitySecret(currentLobby));
//            }
            // Finally, update the currentLobby activity to our activity
            core.activityManager().updateActivity(activity);
        }
    }

    public void clearActivity() {
        core.activityManager().clearActivity();
    }

    public Core getCore() {
        return core;
    }

    public void openVoiceSettings() {
        core.overlayManager().openVoiceSettings();
    }

    public void inviteToGuild(String inviteCode) {
        core.overlayManager().openGuildInvite(inviteCode);
    }

    public void connectToDiscord() {
        // Set parameters for the Core
//            CreateParams params;
        try {
//            disconnectLobby();
//                params = new CreateParams();
            CreateParams params = new CreateParams();

//            params.setClientID(698611073133051974L);
            params.setFlags(CreateParams.getDefaultFlags());
            params.setClientID(1209174746605031444L);
            params.registerEventHandler(this);

            // Create the Core
            core = new Core(params);
            updateActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FunctionalInterface
    public interface ISearchQuery {
        LobbySearchQuery configureSearch(LobbySearchQuery query);
    }
}

