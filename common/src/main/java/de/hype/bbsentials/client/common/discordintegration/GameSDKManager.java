package de.hype.bbsentials.client.common.discordintegration;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.Islands;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GameSDKManager {
    private AtomicBoolean stop = new AtomicBoolean(false);
    private Core core;

    public GameSDKManager() throws Exception {
        // Initialize the Core
        if (core == null) {
            File nativeLibrary = downloadNativeLibrary();
            if (nativeLibrary == null)
                throw new RuntimeException("Could not obtain the Native Library which is required!");
            Core.init(nativeLibrary);

            // Set parameters for the Core
//            CreateParams params;
            try {
//                params = new CreateParams();
                CreateParams params = new CreateParams();

//            params.setClientID(698611073133051974L);
                params.setFlags(CreateParams.getDefaultFlags());
                params.setClientID(1209174746605031444L);
                params.registerEventHandler(new SDKEventListener());

                // Create the Core
                core = new Core(params);
                updateActivity();
            } catch (Exception e) {

            }
            BBsentials.executionService.execute(() -> {
                while (!stop.get()) {
                    runContinously();
                }
            });
        }

    }

    public static File downloadNativeLibrary() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String version = "2.5.6";
        File tempDir = new File(EnvironmentCore.utils.getConfigPath(), name);

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

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
        if (arch.equals("amd64"))
            arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/" + arch + "/" + name + suffix;
        File pathToLibrary = new File(new File(EnvironmentCore.utils.getConfigPath(), "discord_game_sdk"), "discord_game_sdk_" + version + suffix);
        if (pathToLibrary.exists()) return pathToLibrary;
        System.out.println("Downloading SDK");
        // Open the URL as a ZipInputStream
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/" + version + "/discord_game_sdk.zip");
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestProperty("User-Agent", "discord-game-sdk4j (https://github.com/JnCrMx/discord-game-sdk4j)");
        ZipInputStream zin = new ZipInputStream(connection.getInputStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
//                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!(tempDir.mkdir() || tempDir.exists()))
                    throw new IOException("Cannot create temporary directory");

                // Create a temporary file inside our directory (with a "normal" name)
                File temp = new File(tempDir, name + "_" + version + suffix);

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                return temp;
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return null;
    }

    public void stop() {
        stop.set(true);
    }

    public void runContinously() {
        core.runCallbacks();
        try {
            // Sleep a bit to save CPU
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateActivity() {
        // Create the Activity
        try (Activity activity = new Activity()) {
            if (BBsentials.developerConfig.devMode) {
                activity.setDetails("Programming this ↑");
                activity.setState("Developer Mode: Enabled");
                activity.assets().setSmallText("BBsentials. A mod by @hackthetime");
                activity.assets().setSmallImage("bingo_hub");
                activity.assets().setLargeImage("i_am_root_backup_laugh");
                activity.timestamps().setStart(Instant.now());
            }
            else {
                Islands island = EnvironmentCore.utils.getCurrentIsland();
                if (island != null) {
                    activity.setDetails("Playing Hypixel Skyblock");
                    activity.setState(EnvironmentCore.utils.getServerId() + ": " + EnvironmentCore.utils.getCurrentIsland().getDisplayName());
                }
                else {
                    activity.setDetails("Playing Minecraft");
                    activity.setState("");
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
            activity.party().setID("Party");
            activity.secrets().setJoinSecret("party me");

            // Finally, update the current activity to our activity
            core.activityManager().updateActivity(activity);
        }
    }

    public void clearActivity() {
        core.activityManager().clearActivity();
    }
}

class SDKEventListener extends DiscordEventAdapter {
    @Override
    public void onActivityJoinRequest(DiscordUser user) {
        super.onActivityJoinRequest(user);
    }
}