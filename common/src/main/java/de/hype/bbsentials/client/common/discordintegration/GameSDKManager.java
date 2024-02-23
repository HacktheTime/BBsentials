package de.hype.bbsentials.client.common.discordintegration;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.InterceptPacketInfo;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.packets.network.DiscordLobbyPacket;
import de.hype.bbsentials.shared.packets.network.RequestUserInfo;
import de.jcm.discordgamesdk.*;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityJoinRequestReply;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.lobby.LobbyTransaction;
import de.jcm.discordgamesdk.lobby.LobbyType;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GameSDKManager extends DiscordEventAdapter {
    private final String mcUsername = BBsentials.generalConfig.getUsername();
    private AtomicBoolean stop = new AtomicBoolean(false);
    private Core core;
    private Lobby currentLobby;

    private Map<Long, DiscordLobbyUser> members = new HashMap<>();

    public GameSDKManager() throws Exception {
        // Initialize the Core
        if (core == null) {
            File nativeLibrary = downloadNativeLibrary();
            if (nativeLibrary == null)
                throw new RuntimeException("Could not obtain the Native Library which is required!");
            Core.init(nativeLibrary);
            BBsentials.executionService.execute(() -> {
                while (!stop.get()) {
                    runContinously();
                }
            });
            connectToDiscord();
            BBsentials.executionService.scheduleAtFixedRate(this::updateActivity, 1, 1, TimeUnit.MINUTES);
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
        try {
            // Sleep a bit to save CPU
            core.runCallbacks();
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
                activity.assets().setLargeImage("i_am_root_backup_laugh");
                activity.assets().setLargeText("I am Root (→ Linux for I'm the Admin)");
                activity.timestamps().setStart(Instant.now());
                activity.assets().setSmallText("BBsentials. A mod by @hackthetime");
                activity.assets().setSmallImage("bingo_hub");
            }
            else {
                Islands island = EnvironmentCore.utils.getCurrentIsland();
                if (island != null) {
                    activity.setDetails("Playing Hypixel Skyblock");
                    activity.setState(EnvironmentCore.utils.getServerId() + ": " + EnvironmentCore.utils.getCurrentIsland().getDisplayName());
                    activity.assets().setSmallText("BBsentials. A mod by @hackthetime");
                    activity.assets().setSmallImage("bingo_hub");
                    activity.assets().setLargeImage("bingo_card");
                }
                else {
                    activity.setDetails("Playing Minecraft");
                    activity.setState("");
                    activity.assets().setLargeText("BBsentials. A mod by @hackthetime");
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
            if (currentLobby == null) blockingCreateDefaultLobby();
            activity.party().setID(String.valueOf(currentLobby.getId()));
            if (BBsentials.discordConfig.useRPCJoin)
                activity.secrets().setJoinSecret(getLobbyManager().getLobbyActivitySecret(currentLobby));
            if (BBsentials.discordConfig.useRPCSpectate) {
//                activity.secrets().setSpectateSecret(getLobbyManager().getLobbyActivitySecret(currentLobby));
            }
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

    @Override
    public void onActivityJoinRequest(DiscordUser user) {
        if (!BBsentials.connection.isConnected()) BBsentials.conditionalReconnectToBBserver();
        AtomicReference<String> username = new AtomicReference<>("");
        if (user.getUsername().equals("hackthetime")) username.set("Hype_the_Time");
        else if (user.getUsername().equals("ooffyy")) username.set("ooffyy");
        else if (user.getUsername().equals("mininoob46")) username.set("mininoob46");
        else {
            BBsentials.connection.sendPacket(RequestUserInfo.fromDCUserID(user.getUserId(), false));
            BBsentials.connection.packetIntercepts.add(new InterceptPacketInfo<RequestUserInfo>(RequestUserInfo.class, true, true, false, false) {
                @Override
                public void run(RequestUserInfo packet) {
                    if (packet.mcUsername == null) {
                        core.activityManager().sendRequestReply(user.getUserId(), ActivityJoinRequestReply.NO);
                        Chat.sendPrivateMessageToSelfError("BB: DC RPC: DC username: " + user.getUsername() + " requested to join but was denied cause they are not registered.");
                        return;
                    }
                    if (packet.isUserPunished()) {
                        core.activityManager().sendRequestReply(user.getUserId(), ActivityJoinRequestReply.NO);
                        Chat.sendPrivateMessageToSelfError("BB: DC RPC: DC username: " + user.getUsername() + " requested to join but was denied since their is a punishment ongoing");
                        return;
                    }
                    acceptUserJoinRequest(user, mcUsername, packet.hasRole("mod"));
                }
            });
        }
        acceptUserJoinRequest(user, username.get(), true);
        //Call when someone wants to join me
    }

    public void acceptUserJoinRequest(DiscordUser user, String mcUsername, boolean shallBypass) {
        core.activityManager().sendRequestReply(user.getUserId(), ActivityJoinRequestReply.YES);
        BBsentials.sender.addSendTask("/p " + mcUsername, 1.5);
        if (currentLobby == null) {
            LobbyManager manager = BBsentials.dcGameSDK.getCore().lobbyManager();
            LobbyTransaction txn = manager.getLobbyCreateTransaction();
            txn.setType(LobbyType.PUBLIC);
            txn.setCapacity(50);
            txn.setLocked(false);
            txn.setMetadata("hoster", mcUsername);
            manager.createLobby(manager.getLobbyCreateTransaction(), ((result, lobby) -> {
                this.currentLobby = lobby;
            }));
        }
        else {
            if (getLobbyManager().getMemberUsers(currentLobby).size() == currentLobby.getCapacity() && shallBypass) {
                LobbyTransaction trx = getLobbyManager().getLobbyCreateTransaction();
                trx.setCapacity(currentLobby.getCapacity() + 1);
                trx.setMetadata("hoster", mcUsername);
                getLobbyManager().updateLobby(currentLobby, trx);
            }
            if (BBsentials.discordConfig.connectVoiceOnJoining) {
                getLobbyManager().connectVoice(currentLobby);
            }
        }

    }

    public LobbyManager getLobbyManager() {
        return getCore().lobbyManager();
    }

    public void disconnectFromLobby(Lobby lobby) {
        try {
            getLobbyManager().disconnectLobby(lobby);
        } catch (Exception e) {

        }
        try {
            getLobbyManager().disconnectVoice(lobby);
        } catch (Exception e) {

        }
    }

    public void disconnectFromLobbyVC(Lobby lobby) {
        try {
            getLobbyManager().disconnectVoice(lobby);
        } catch (Exception e) {

        }
    }

    @Override
    public void onActivityJoin(String secret) {
        if (currentLobby != null) disconnectLobby();
        Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC join Request: Working...");
        BBsentials.executionService.schedule(() -> {
            LobbyManager manager = BBsentials.dcGameSDK.getCore().lobbyManager();
            manager.connectLobbyWithActivitySecret(secret, ((result, lobby) -> {
                if (!result.equals(Result.OK)) {
                    Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC join Request failed: " + result);
                    return;
                }
                manager.connectNetwork(lobby);
                Chat.sendPrivateMessageToSelfSuccess("BB: DISCORD RPC join: Success");
                if (BBsentials.discordConfig.connectVoiceOnJoin) {
                    manager.connectVoice(lobby, result2 -> {
                        if (result2.equals(Result.OK))
                            Chat.sendPrivateMessageToSelfSuccess("BB: DISCORD RPC Voice Connect: Success");
                        else if (result2.equals(Result.LOBBY_FULL))
                            Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC Voice Connect: Lobby Full");
                        else Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC Voice Connect: " + result2);
                    });

                }
            }));
            initMembers();
        }, 10, TimeUnit.SECONDS);
        //Call for when I join a lobby // request too
    }

    @Override
    public void onActivitySpectate(String secret) {
        if (currentLobby != null) disconnectLobby();
        Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC join Request: Working...");
        BBsentials.executionService.schedule(() -> {
            LobbyManager manager = BBsentials.dcGameSDK.getCore().lobbyManager();
            manager.connectLobbyWithActivitySecret(secret, ((result, lobby) -> {
                if (!result.equals(Result.OK)) {
                    Chat.sendPrivateMessageToSelfError("BB: DISCORD RPC join Request failed: " + result);
                    return;
                }
                manager.connectNetwork(lobby);
                Chat.sendPrivateMessageToSelfSuccess("BB: DISCORD RPC join: Success");
            }));
            initMembers();
        }, 10, TimeUnit.SECONDS);
    }

    public LobbySearchQuery getSearch() {
        return getLobbyManager().getSearchQuery();
    }

    public LobbyTransaction updateLobby(Lobby lobby) {
        return getLobbyManager().getLobbyUpdateTransaction(lobby);
    }

    @Override
    public void onLobbyMessage(long lobbyId, long userId, byte[] data) {
        super.onLobbyMessage(lobbyId, userId, data);
    }

    public List<DiscordUser> getLobbyMembers() {
        if (currentLobby == null) return new ArrayList<>();
        return getLobbyManager().getMemberUsers(currentLobby);
    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void createLobby(LobbyTransaction transaction) {
        disconnectLobby();
        getLobbyManager().createLobby(transaction, (result, lobby) -> currentLobby = lobby);
        initMembers();
    }

    public void updateCurrentLobby(LobbyTransaction transaction) {
        getLobbyManager().updateLobby(currentLobby, transaction);
    }

    public void blockingCreateDefaultLobby() {
        if (currentLobby != null) disconnectLobby();
        AtomicReference<Lobby> lobby = new AtomicReference<>(null);
        LobbyTransaction trn = getLobbyManager().getLobbyCreateTransaction();
        trn.setCapacity(15);
        if (BBsentials.discordConfig.discordRoomsDefaultPrivate) trn.setType(LobbyType.PRIVATE);
        else trn.setType(LobbyType.PUBLIC);
        trn.setLocked(false);
        trn.setMetadata("hoster", mcUsername);
        getLobbyManager().createLobby(trn, lobby::set);
        while (lobby.get() == null) {
            try {
                Thread.sleep(100);
                core.runCallbacks();
            } catch (InterruptedException ignored) {

            }
        }
        currentLobby = lobby.get();
        initMembers();
    }

    public void openVoiceSettings() {
        core.overlayManager().openVoiceSettings();
    }

    public void inviteToGuild(String inviteCode) {
        core.overlayManager().openGuildInvite(inviteCode);
    }

    @Override
    public void onSpeaking(long lobbyId, long userId, boolean speaking) {
        members.get(userId).setIsTalking(speaking);
    }

    public void blockingJoinLobby(Long lobbyId, String secret) {
        if (currentLobby != null) getLobbyManager().disconnectLobby(currentLobby);
        currentLobby = null;
        getLobbyManager().connectLobby(lobbyId, secret, (result, lobby) -> currentLobby = lobby);
        while (currentLobby == null) {
            try {
                Thread.sleep(100);
                core.runCallbacks();
            } catch (InterruptedException e) {
            }
        }
        initMembers();
    }

    private void initMembers() {
        members.clear();
        BBsentials.executionService.execute(() -> {
            for (DiscordUser lobbyMember : getLobbyMembers()) {
                try {
                    if (members.get(lobbyMember.getUserId()) == null) {
                        DiscordLobbyUser user = new DiscordLobbyUser(lobbyMember, getCurrentLobby());
                        if (user == null) throw new RuntimeException("Uhm here");
                        members.put(lobbyMember.getUserId(), user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void joinVC() {
        getLobbyManager().connectVoice(currentLobby);
    }

    public void connectToDiscord() {
        // Set parameters for the Core
//            CreateParams params;
        try {
            currentLobby = null;
//                params = new CreateParams();
            CreateParams params = new CreateParams();

//            params.setClientID(698611073133051974L);
            params.setFlags(CreateParams.getDefaultFlags());
            params.setClientID(1209174746605031444L);
            params.registerEventHandler(this);

            // Create the Core
            core = new Core(params);
            updateActivity();
            initMembers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectLobby() {
        disconnectFromLobby(currentLobby);
        currentLobby = null;
        members.clear();
    }

    public void blockingJoinLobbyWithActivitySecret(String activitySecret) {
        if (currentLobby != null) getLobbyManager().disconnectLobby(currentLobby);
        members.clear();
        currentLobby = null;
        getLobbyManager().connectLobbyWithActivitySecret(activitySecret, (result, lobby) -> currentLobby = lobby);
        while (currentLobby == null) {
            try {
                Thread.sleep(100);
                core.runCallbacks();
            } catch (InterruptedException e) {
            }
        }
        initMembers();
    }

    public List<Lobby> blockingSearch(ISearchQuery query) {
        getLobbyManager().search(query.configureSearch(getLobbyManager().getSearchQuery()));
        return getLobbyManager().getLobbies();
    }

    public void deleteLobby() {
        deleteLobby(currentLobby);
        currentLobby = null;
        members.clear();
    }

    public void deleteLobby(Lobby lobby) {
        getLobbyManager().deleteLobby(lobby);
    }

    public void disconnectLobbyVC() {
        disconnectFromLobbyVC(currentLobby);
    }

    @Override
    public void onMemberConnect(long lobbyId, long userId) {
        initMembers();
    }

    public DiscordLobbyPacket getLobbyAsPacket() {
        DiscordLobbyPacket.Type type = DiscordLobbyPacket.Type.PUBLIC;
        if (currentLobby.getType().equals(LobbyType.PRIVATE)) type = DiscordLobbyPacket.Type.PRIVATE;
        return new DiscordLobbyPacket(getLobbyMembers().stream().map(DiscordUser::getUserId).collect(Collectors.toList()), type, currentLobby.getId(), currentLobby.getSecret(), currentLobby.getOwnerId(), currentLobby.getCapacity(), currentLobby.isLocked(), getLobbyManager().getLobbyMetadata(currentLobby));
    }

    public List<DiscordLobbyUser> getAdvancedLobbyMembers() {
        if (members == null || members.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(members.values());
    }

    @Override
    public void onMemberDisconnect(long lobbyId, long userId) {
        initMembers();
    }


    @FunctionalInterface
    public interface ISearchQuery {
        LobbySearchQuery configureSearch(LobbySearchQuery query);
    }
}

