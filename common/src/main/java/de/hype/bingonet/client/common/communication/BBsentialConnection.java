package de.hype.bingonet.client.common.communication;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.chat.Message;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.client.SplashManager;
import de.hype.bingonet.client.common.client.objects.ServerSwitchTask;
import de.hype.bingonet.client.common.client.updatelisteners.SplashStatusUpdateListener;
import de.hype.bingonet.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bingonet.client.common.config.PartyManager;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.client.common.objects.ChatPrompt;
import de.hype.bingonet.client.common.objects.InterceptPacketInfo;
import de.hype.bingonet.client.common.objects.Waypoints;
import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.environment.packetconfig.PacketManager;
import de.hype.bingonet.environment.packetconfig.PacketUtils;
import de.hype.bingonet.shared.constants.*;
import de.hype.bingonet.shared.objects.ChChestData;
import de.hype.bingonet.shared.objects.ChestLobbyData;
import de.hype.bingonet.shared.objects.ClientWaypointData;
import de.hype.bingonet.shared.objects.Position;
import de.hype.bingonet.shared.packets.function.*;
import de.hype.bingonet.shared.packets.mining.ChChestPacket;
import de.hype.bingonet.shared.packets.mining.MiningEventPacket;
import de.hype.bingonet.shared.packets.network.*;
import org.apache.commons.text.StringEscapeUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BBsentialConnection {
    public Thread messageReceiverThread;
    public Thread messageSenderThread;
    public List<InterceptPacketInfo> packetIntercepts = new ArrayList();
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private LinkedBlockingQueue<String> messageQueue;
    private PacketManager packetManager;
    private Boolean authenticated = null;

    public BBsentialConnection() {
        UpdateListenerManager.resetListeners();
        packetManager = new PacketManager(this);
    }


    public static boolean isCommandSafe(String command) {
        if (command.startsWith("/p ") || command.startsWith("/party ") || command.startsWith("/boop ") || command.startsWith("/msg ") || command.startsWith("/hub ")) {
            return true;
        } else {
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! " + command + " please check if its safe. IMMEDIATELY report this to the Admins and DeveloperAbstractConfig Hype_the_Time (@hackthetime). If it is not safe immediately remove BingoNet!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelfFatal("§4" + emergencyMessage + "\n\n");
        }
        return false;
    }

    public static boolean selfDestruct() {
        try {
            // Get the path to the running JAR file
            String jarFilePath = BingoNet.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();

            // Create a File object for the JAR file
            File jarFile = new File(jarFilePath);

            // Check if the JAR file exists
            if (jarFile.exists()) {
                // Delete the JAR file
                return jarFile.delete();
            } else {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    public void connect(String serverIP, int serverPort) {
        // Enable SSL handshake debugging
        System.setProperty("javax.net.debug", "ssl,handshake");
        FileInputStream inputStream = null;
        try {
            if (!serverIP.equals("localhost")) {
                // Load the BBssentials-online server's public certificate from the JKS file
                try {
                    InputStream resouceInputStream = BingoNet.class.getResourceAsStream("/assets/public_bingonet_cert.crt");

                    // Check if the resource exists
                    if (resouceInputStream == null) {
                        System.out.println("The resource '/assets/public_bingonet_cert.crt' was not found.");
                        return;
                    }

                    File tempFile = File.createTempFile("public_bingonet_cert", ".crt");
                    tempFile.deleteOnExit();

                    FileOutputStream outputStream = new FileOutputStream(tempFile);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = resouceInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    resouceInputStream.close();

                    // Now you have the .crt file as a FileInputStream in the tempFile
                    inputStream = new FileInputStream(tempFile);

                    // Use the fileInputStream as needed

                } catch (IOException e) {
                    e.printStackTrace();
                }
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate serverCertificate = (X509Certificate) certFactory.generateCertificate(inputStream);
                PublicKey serverPublicKey = serverCertificate.getPublicKey();

                // Create a TrustManager that trusts only the server's public key
                TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null; // We don't need to check the client's certificates
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        // Do nothing, client certificate validation not required
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        // Check if the server certificate matches the expected one
                        if (certs.length == 1) {
                            PublicKey publicKey = certs[0].getPublicKey();
                            if (!publicKey.equals(serverPublicKey)) {
                                throw new CertificateException("Server certificate not trusted.");
                            }
                        } else {
                            throw new CertificateException("Invalid server certificate chain.");
                        }
                    }
                }};

                // Create an SSL context with the custom TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, new SecureRandom());
                // Create an SSL socket factory
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                socket = sslSocketFactory.createSocket(serverIP, serverPort);
            } else {
                try {
                    socket = new Socket(serverIP, serverPort);
                } catch (Exception e) {
                    Chat.sendPrivateMessageToSelfError("Error trying to connect: %s".formatted(e.getMessage()));
                }
            }
            socket.setKeepAlive(true); // Enable Keep-Alive
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            messageQueue = new LinkedBlockingQueue<>();

            // Start message receiver thread
            messageReceiverThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        String message = reader.readLine();
                        if (message != null) {
                            onMessageReceived(message);
                        } else {
                            Chat.sendPrivateMessageToSelfError("BB: It seemed like you disconnected.");
                            try {
                                Thread.sleep(10000);
                            } catch (Exception e) {

                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageReceiverThread.start();
            messageReceiverThread.setName("bb receiver thread");
            // Start message sender thread
            messageSenderThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        String message = messageQueue.take();
                        if (BingoNet.developerConfig.isDetailedDevModeEnabled())
                            Chat.sendPrivateMessageToSelfDebug("BBs: " + message);
                        writer.println(message);
                    }
                } catch (InterruptedException | NullPointerException ignored) {
                }
            });
            messageSenderThread.start();
            messageSenderThread.setName("bb sender thread");

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            if (e instanceof ConnectException) {
                System.out.println("Error trying to connect to %s on port %s".formatted(serverIP, serverPort));
            }
            e.printStackTrace();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        if (messageQueue != null) {
            Chat.sendPrivateMessageToSelfDebug("BBs: " + message);
            messageQueue.offer(message);
        } else {
            Chat.sendPrivateMessageToSelfError("BB: It seems like the connection was lost. Please try to reconnect with /bbi reconnect");
        }
    }

    public void sendHiddenMessage(String message) {
        if (isConnected()) {
            if (BingoNet.developerConfig.isDetailedDevModeEnabled()) {
                Chat.sendPrivateMessageToSelfDebug("BBDev-s: " + message);
            }
            try {
                if (socket.isConnected() && writer != null) {
                    if (BingoNet.developerConfig.isDetailedDevModeEnabled())
                        Chat.sendPrivateMessageToSelfDebug("BBHs: " + message);
                    writer.println(message);
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    public void onMessageReceived(String message) {
        if (!PacketUtils.handleIfPacket(this, message)) {
            if (message.startsWith("H-")) {
            } else {
                Chat.sendPrivateMessageToSelfSuccess("BB: " + message);
            }
        }
    }

    public <T extends AbstractPacket> void dummy(T o) {
        //this does absolutely nothing. dummy for packet in packt manager
    }

    public <E extends AbstractPacket> void sendPacket(E packet) {
        String packetName = packet.getClass().getSimpleName();
        String rawjson = PacketUtils.parsePacketToJson(packet);
        if (isConnected() && writer != null) {
            if (BingoNet.developerConfig.isDetailedDevModeEnabled() && !((packet.getClass().equals(RequestConnectPacket.class) && !BingoNet.bbServerConfig.useMojangAuth) && BingoNet.developerConfig.devSecurity)) {
                Chat.sendPrivateMessageToSelfDebug("BBDev-sP: " + packetName + ": " + rawjson);
            }
            writer.println(packetName + "." + rawjson);
        } else {
            Chat.sendPrivateMessageToSelfError("BB: Couldn't send a " + packetName + "! did you get disconnected?");
        }
    }

    public void onBroadcastMessagePacket(BroadcastMessagePacket packet) {
        Chat.sendPrivateMessageToSelfImportantInfo("[A] §r[" + packet.prefix + "§r]§6 " + packet.username + ": " + packet.message);
    }

    public void onSplashNotifyPacket(SplashNotifyPacket packet) {
        //influencing the delay in any way is disallowed!
        int waitTime;
        if (packet.splash.announcer.equals(BingoNet.generalConfig.getUsername()) && BingoNet.splashConfig.autoSplashStatusUpdates) {
            Chat.sendPrivateMessageToSelfInfo("The Splash Update Statuses will be updatet automatically for you. If you need to do something manually go into Discord Splash Dashboard");
            SplashStatusUpdateListener splashStatusUpdateListener = new SplashStatusUpdateListener(packet.splash);
            UpdateListenerManager.splashStatusUpdateListener = splashStatusUpdateListener;
            BingoNet.executionService.execute(splashStatusUpdateListener);
        } else {
            SplashManager.addSplash(packet.splash);
            if (packet.splash.lessWaste) {
                waitTime = Math.min(((EnvironmentCore.utils.getPotTime() * 1000) / 80), 25 * 1000);
            } else {
                waitTime = 0;
            }
            BingoNet.executionService.schedule(() -> {
                SplashManager.display(packet.splash.splashId);
            }, waitTime, TimeUnit.MILLISECONDS);
        }
    }

    public void onBingoChatMessagePacket(BingoChatMessagePacket packet) {
        if (BingoNet.visualConfig.showBingoChat) {
            Chat.sendPrivateMessageToSelfInfo("[" + packet.prefix + "§r] " + packet.username + ": " + packet.message);
        }
    }

    public void onMiningEventPacket(MiningEventPacket packet) {
        if (BingoNet.miningEventConfig.blockChEvents && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
            return;
        if (!(BingoNet.miningEventConfig.allEvents)) {
            if (packet.event.equals(MiningEvents.RAFFLE)) {
                if (!BingoNet.miningEventConfig.raffle) return;
            } else if (packet.event.equals(MiningEvents.GOBLIN_RAID)) {
                if (!BingoNet.miningEventConfig.goblinRaid) return;
            } else if (packet.event.equals(MiningEvents.MITHRIL_GOURMAND)) {
                if (!BingoNet.miningEventConfig.mithrilGourmand) return;
            } else if (packet.event.equals(MiningEvents.BETTER_TOGETHER)) {
                if (BingoNet.miningEventConfig.betterTogether.equals("none")) return;
                if (BingoNet.miningEventConfig.betterTogether.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BingoNet.miningEventConfig.betterTogether.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            } else if (packet.event.equals(MiningEvents.DOUBLE_POWDER)) {
                if (BingoNet.miningEventConfig.doublePowder.equals("none")) return;
                if (BingoNet.miningEventConfig.doublePowder.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BingoNet.miningEventConfig.doublePowder.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            } else if (packet.event.equals(MiningEvents.GONE_WITH_THE_WIND)) {
                if (BingoNet.miningEventConfig.goneWithTheWind.equals("none")) return;
                if (BingoNet.miningEventConfig.goneWithTheWind.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BingoNet.miningEventConfig.goneWithTheWind.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            }
        }
        Chat.sendPrivateMessageToSelfImportantInfo(packet.username + ": There is a " + packet.event.getDisplayName() + " in the " + packet.island.getDisplayName() + " now/soon.");
    }


    public void onWelcomePacket(WelcomeClientPacket packet) {
        authenticated = packet.success;
        if (packet.success) {
            BingoNet.generalConfig.bingonetRoles = new HashSet<>(packet.roles);
            Chat.sendPrivateMessageToSelfSuccess("Login Success");
            if (socket.getRemoteSocketAddress().toString().startsWith("localhost"))
                Chat.sendPrivateMessageToSelfError("You are connected to the Local Test Server!");
            if (!packet.motd.isEmpty()) {
                Chat.sendPrivateMessageToSelfImportantInfo(packet.motd);
            }
        } else {
            Chat.sendPrivateMessageToSelfError("Login Failed");
        }
    }

    public void onDisconnectPacket(DisconnectPacket packet) {
        if (EnvironmentCore.utils.isInGame()) {
            Chat.sendPrivateMessageToSelfError(packet.displayMessage);
            try {
                BingoNet.connection.close();
            } catch (Exception ignored) {
            }
            for (int i = 0; i < packet.waitBeforeReconnect.length; i++) {
                int finalI = i;
                BingoNet.executionService.schedule(() -> {
                    if (finalI == 0) {
                        BingoNet.connectToBBserver();
                    } else {
                        BingoNet.conditionalReconnectToBBserver();
                    }
                }, (long) (packet.waitBeforeReconnect[i] + (Math.random() * packet.randomExtraDelay)), TimeUnit.SECONDS);
            }
        } else {
            if (packet.internalReason.equals(InternalReasonConstants.NOT_REGISTERED))
                EnvironmentCore.utils.showErrorScreen("Could not connect to the network. Reason: \n" + packet.displayMessage);
            else EnvironmentCore.utils.showErrorScreen(packet.displayMessage);
        }
    }

    public void onDisplayTellrawMessagePacket(DisplayTellrawMessagePacket packet) {
        /*Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(packet.message));*/
        Chat.sendPrivateMessageToSelfImportantInfo("You received a tellraw Packet but it got ignored due too there being no safety checks in this version.");
    }

    public void onInternalCommandPacket(InternalCommandPacket packet) {
        if (packet.command.equals(InternalCommandPacket.REQUEST_POT_DURATION)) {
            sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_POT_DURATION, new String[]{String.valueOf(EnvironmentCore.utils.getPotTime())}));
        } else if (packet.command.equals(InternalCommandPacket.SELFDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated. Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty())
                Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.utils.playsound("block.anvil.destroy");
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                BingoNet.executionService.schedule(() -> Chat.sendPrivateMessageToSelfFatal("BB: Time till crash: " + finalI), i, TimeUnit.SECONDS);
            }
            throw new RuntimeException("BingoNet: Self Remove was triggered");
        } else if (packet.command.equals(InternalCommandPacket.PEACEFULLDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated! Becomes effective on next launch");
            if (!packet.parameters[0].isEmpty())
                Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.utils.playsound("block.anvil.destroy");
        } else if (packet.command.equals(InternalCommandPacket.HUB)) {
            BingoNet.sender.addImmediateSendTask("/hub");
        } else if (packet.command.equals(InternalCommandPacket.PRIVATE_ISLAND)) {
            BingoNet.sender.addImmediateSendTask("/is");
        } else if (packet.command.equals(InternalCommandPacket.HIDDEN_HUB)) {
            BingoNet.sender.addHiddenSendTask("/hub", 0);
        } else if (packet.command.equals(InternalCommandPacket.HIDDEN_PRIVATE_ISLAND)) {
            BingoNet.sender.addHiddenSendTask("/is", 0);
        } else if (packet.command.equals(InternalCommandPacket.CRASH)) {
            Chat.sendPrivateMessageToSelfFatal("BB: Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty())
                Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            Thread crashThread = new Thread(() -> {
                EnvironmentCore.utils.playsound("block.anvil.destroy");
                for (int i = 10; i >= 0; i--) {
                    Chat.sendPrivateMessageToSelfFatal("BB: Time till crash: " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
                EnvironmentCore.utils.systemExit(69);
            });
            crashThread.start();
        } else if (packet.command.equals(InternalCommandPacket.INSTACRASH)) {
            System.out.println("BingoNet: InstaCrash triggered");
            EnvironmentCore.utils.systemExit(69);
        }
    }

    public void onInvalidCommandFeedbackPacket(InvalidCommandFeedbackPacket packet) {
        Chat.sendPrivateMessageToSelfError(packet.displayMessage);
    }

    public void onPartyPacket(PartyPacket packet) {
        if (BingoNet.partyConfig.allowServerPartyInvite) {
            boolean isInParty = PartyManager.isInParty();
            if (!isInParty && !(packet.type == PartyConstants.JOIN || packet.type == PartyConstants.ACCEPT || packet.type == PartyConstants.INVITE))
                return;
            boolean leader = PartyManager.isPartyLeader();
            boolean moderator = PartyManager.isModerator();

            if (packet.type == PartyConstants.JOIN) {
                Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party join");
                if (isInParty) BingoNet.sender.addSendTask("/p leave");
                BingoNet.sender.addSendTask("/p join " + packet.users.getFirst());
            } else if (packet.type == PartyConstants.ACCEPT) {
                if (isInParty) BingoNet.sender.addSendTask("/p leave");
                Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party accept");
                BingoNet.sender.addSendTask("/p accept " + packet.users.getFirst());
            } else if (packet.type == PartyConstants.DISBAND) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party disband");
                    Chat.sendCommand("/p disband");
                } else {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party disband but you are not the leader. Leaving party");
                    Chat.sendCommand("/p leave");
                }
            } else if (packet.type == PartyConstants.INVITE) {
                if (!isInParty || leader || moderator) {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party invite");
                    List<String> users = packet.users;
                    int chunkSize = 5;
                    for (int i = 0; i < users.size(); i += chunkSize) {
                        List<String> chunk = users.subList(i, Math.min(users.size(), i + chunkSize));
                        Chat.sendCommand("/p invite " + String.join(" ", chunk));
                    }
                } else {
                    BingoNet.sender.addSendTask("/pc Bingo Net Server requested a party invite for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.WARP) {
                if (leader || moderator) {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party warp");
                    Chat.sendCommand("/p warp");
                } else {
                    BingoNet.sender.addSendTask("/pc Bingo Net Server requested a party warp");
                }
            } else if (packet.type == PartyConstants.KICK) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party kick");
                    packet.users.forEach((u) -> BingoNet.sender.addSendTask("/p kick %s".formatted(u)));
                } else {
                    BingoNet.sender.addSendTask("/pc Bingo Net Server requested a party kicks for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.PROMOTE) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party promote");
                    Chat.sendCommand("/p promote " + packet.users.get(0));
                } else {
                    BingoNet.sender.addSendTask("/pc Bingo Net Server requested party promotion for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.LEAVE) {
                Chat.sendPrivateMessageToSelfInfo("Bingo Net Server requested party leave");
                Chat.sendCommand("/p leave");
            }

        } else {
            List<String> users = packet.users;
            int chunkSize = 5;
            for (int i = 0; i < users.size(); i += chunkSize) {
                List<String> chunk = users.subList(i, Math.min(users.size(), i + chunkSize));
                Chat.sendCommand("/p " + packet.type.toString().toLowerCase() + " " + String.join(" ", chunk));
            }
        }
    }

    public void onSystemMessagePacket(SystemMessagePacket packet) {
        if (packet.important) {
            Chat.sendPrivateMessageToSelfImportantInfo("§n" + packet.message);
        } else {
            Chat.sendPrivateMessageToSelfInfo(packet.message);
        }
        if (packet.ping) {
            EnvironmentCore.utils.playsound("block.anvil.destroy");
        }
    }

    public void onRequestAuthentication(RequestAuthentication packet) {
        if (socket.getPort() == 5011) {
            Chat.sendPrivateMessageToSelfSuccess("Logging into BingoNet-online (Beta Development Server)");
            Chat.sendPrivateMessageToSelfImportantInfo("You may test here but do NOT Spam unless you have very good reasons. Spamming may still be punished");
        } else {
            Chat.sendPrivateMessageToSelfSuccess("Logging into BingoNet-online");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Random r1 = new Random();
        Random r2 = new Random(System.identityHashCode(new Object()));
        BigInteger random1Bi = new BigInteger(64, r1);
        BigInteger random2Bi = new BigInteger(64, r2);
        BigInteger serverBi = random1Bi.xor(random2Bi);
        String clientRandom = serverBi.toString(16);

        String serverId = clientRandom + packet.serverIdSuffix;

        if (BingoNet.bbServerConfig.useMojangAuth) {
            EnvironmentCore.utils.mojangAuth(serverId);
            RequestConnectPacket connectPacket = new RequestConnectPacket(BingoNet.generalConfig.getMCUUID(), clientRandom, EnvironmentCore.utils.getModVersion(), EnvironmentCore.utils.getGameVersion(), BingoNet.generalConfig.getApiVersion(), AuthenticationConstants.MOJANG);
            sendPacket(connectPacket);
        } else {
            sendPacket(new RequestConnectPacket(BingoNet.generalConfig.getMCUUID(), BingoNet.bbServerConfig.apiKey, EnvironmentCore.utils.getModVersion(), EnvironmentCore.utils.getGameVersion(), BingoNet.generalConfig.getApiVersion(), AuthenticationConstants.DATABASE));
        }
    }


    public boolean isConnected() {
        try {
            return socket.isConnected() && !socket.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        try {
            if (messageReceiverThread != null) {
                messageReceiverThread.interrupt();
            }
            if (messageSenderThread != null) {
                messageSenderThread.interrupt();
            }
            if (BingoNet.bbthread != null) {
                BingoNet.bbthread.interrupt();
            }
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
            if (messageQueue != null) messageQueue.clear();
            if (BingoNet.bbthread != null) {
                BingoNet.bbthread.join();
                BingoNet.bbthread = null;
            }
            if (messageSenderThread != null) {
                messageSenderThread.join();
                messageSenderThread = null;
            }
            if (messageReceiverThread != null) {
                messageReceiverThread.join();
                messageReceiverThread = null;
            }
            writer = null;
            reader = null;
            socket = null;
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onWaypointPacket(WaypointPacket packet) {
        if (packet.operation.equals(WaypointPacket.Operation.ADD)) {
            new Waypoints(packet.waypoint);
        } else if (packet.operation.equals(WaypointPacket.Operation.REMOVE)) {
            try {
                Waypoints.waypoints.get(packet.waypointId).removeFromPool();
            } catch (Exception ignored) {

            }
        } else if (packet.operation.equals(WaypointPacket.Operation.EDIT)) {
            try {
                Waypoints oldWaypoint = Waypoints.waypoints.get(packet.waypointId);
                oldWaypoint.replaceWithNewWaypoint(packet.waypoint, packet.waypointId);
            } catch (Exception ignored) {

            }
        }
    }

    public void onGetWaypointsPacket(GetWaypointsPacket packet) {
        sendPacket(new GetWaypointsPacket(Waypoints.waypoints.values().stream().map((waypoint -> ((ClientWaypointData) waypoint))).collect(Collectors.toList())));
    }

    public void onCompletedGoalPacket(CompletedGoalPacket packet) {
        if (!BingoNet.visualConfig.showCardCompletions && packet.completionType.equals(CompletedGoalPacket.CompletionType.CARD))
            Chat.sendPrivateMessageToSelfText(Message.tellraw("[\"\",{\"text\":\"@username \",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"just completed the \",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"Bingo\",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"!\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}}]".replace("@username", StringEscapeUtils.escapeJson(packet.username)).replace("@lore", StringEscapeUtils.escapeJson(packet.lore))));
            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"@username "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"just completed the "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"Bingo"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"!"}],"command":"%s","jtemplate":"tellraw"}
        else if (!BingoNet.visualConfig.showGoalCompletions && packet.completionType.equals(CompletedGoalPacket.CompletionType.GOAL))
            Chat.sendPrivateMessageToSelfText(Message.tellraw("[\"\",{\"text\":\"@username \",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"just completed the Goal \",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"@name\",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"!\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}}]".replace("@username", StringEscapeUtils.escapeJson(packet.username)).replace("@lore", StringEscapeUtils.escapeJson(packet.lore)).replace("@name", StringEscapeUtils.escapeJson(packet.name))));
        //["",{"text":"@username ","color":"gold","hoverEvent":{"action":"show_text","contents":["@lore"]}},{"text":"just completed the Goal ","color":"gray","hoverEvent":{"action":"show_text","contents":["@lore"]}},{"text":"@name","color":"gold","hoverEvent":{"action":"show_text","contents":["@lore"]}},{"text":"!","color":"gray","hoverEvent":{"action":"show_text","contents":["@lore"]}}]
    }

    public void onPlaySoundPacket(PlaySoundPacket packet) {
        if (packet.streamFromUrl) EnvironmentCore.utils.streamCustomSound(packet.soundId, packet.durationInSeconds);
        else EnvironmentCore.utils.playsound(packet.soundId);
    }

    public void onWantedSearchPacket(WantedSearchPacket packet) {
        if (packet.serverId != null && !(EnvironmentCore.utils.getServerId().matches(packet.serverId))) return;
        if (packet.mega != null && packet.mega != EnvironmentCore.utils.isOnMegaServer()) return;
        List<String> playerCount = EnvironmentCore.utils.getPlayers();
        if (packet.maximumPlayerCount != null && packet.maximumPlayerCount <= playerCount.size()) return;
        if (packet.minimumPlayerCount != null && packet.minimumPlayerCount >= playerCount.size()) return;
        if (packet.username != null && !playerCount.contains(packet.username)) return;
        sendPacket(packet.preparePacketToReplyToThis(new WantedSearchPacket.WantedSearchPacketReply(BingoNet.generalConfig.getUsername(), EnvironmentCore.utils.getPlayers(), EnvironmentCore.utils.isOnMegaServer(), EnvironmentCore.utils.getServerId())));
    }

    public void onPunishedPacket(PunishedPacket data) {
        if (data.disconnectFromNetworkOnLoad) close();
        if (data.modSelfRemove) selfDestruct();
        if (!data.silentCrash) {
            Chat.sendPrivateMessageToSelfFatal("You have been %sed in the Bingo Net Network!".formatted(data.type));
            if (data.modSelfRemove)
                Chat.sendPrivateMessageToSelfFatal("You are no longer Permitted to use the Mod. The Mod will now automatically Remove itself.");
        }
        if (data.shouldModCrash) {
            for (int i = 0; i < data.warningTimeBeforeCrash; i++) {
                if (!data.silentCrash) Chat.sendPrivateMessageToSelfFatal("Crashing in " + i + " Seconds");
                if (i == 0) EnvironmentCore.utils.systemExit(data.exitCodeOnCrash);
            }
        }

    }

    public void annonceChChest(Position coords, List<ChChestItem> items, String command, String extraMessage) {
        if (UpdateListenerManager.chChestUpdateListener.currentlyInChLobby()) {
            UpdateListenerManager.chChestUpdateListener.addChestAndUpdate(coords, items);
            return;
        }
        if (Instant.now().isAfter(EnvironmentCore.utils.getLobbyClosingTime())) {
            Chat.sendPrivateMessageToSelfError("The Lobby is already Closed (Day Count too high) → No one can be warped in!");
            return;
        }
        if (!BingoNet.partyConfig.allowBBinviteMe && command.trim().equalsIgnoreCase("/msg " + BingoNet.generalConfig.getUsername() + " bb:party me")) {
            Chat.sendPrivateMessageToSelfImportantInfo("Enabled bb:party invites temporarily. Will be disabled on Server swap!");
            BingoNet.partyConfig.allowBBinviteMe = true;
            ServerSwitchTask.onServerLeaveTask(() -> BingoNet.partyConfig.allowBBinviteMe = false);
        } else if (command.trim().equalsIgnoreCase("/p join " + BingoNet.generalConfig.getUsername())) {
            if (!PartyManager.isInParty()) BingoNet.sender.addImmediateSendTask("/p leave");
            BingoNet.sender.addHiddenSendTask("/stream open 23", 1);
            BingoNet.sender.addHiddenSendTask("/pl", 2);
            Chat.sendPrivateMessageToSelfImportantInfo("Opened Stream Party for you since you announced chchest items");
        }

        BingoNet.connection.sendPacket(new ChChestPacket(new ChestLobbyData(List.of(new ChChestData("", coords, items)), EnvironmentCore.utils.getServerId(), command, extraMessage, StatusConstants.OPEN)));
    }

    public void onRequestMinionDataPacket(MinionDataResponse.RequestMinionDataPacket packet) {
        sendPacket(packet.preparePacketToReplyToThis(EnvironmentCore.utils.getMiniondata()));
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void onCommandChatPromptPacket(CommandChatPromptPacket packet) {
        ChatPrompt prompt = new ChatPrompt(() -> {
            for (CommandChatPromptPacket.CommandRecord command : packet.getCommands()) {
                BingoNet.sender.addSendTask(command.command, command.delay);
            }
        }, 10);
        Chat.sendPrivateMessageToSelfText(packet.getPrintMessage());
        BingoNet.temporaryConfig.lastChatPromptAnswer = prompt;
    }

    public void onPacketChatPromptPacket(PacketChatPromptPacket packet) {
        ChatPrompt prompt = new ChatPrompt(() -> {
            for (AbstractPacket p : packet.getPackets()) {
                sendPacket(p);
            }
        }, 10);
        Chat.sendPrivateMessageToSelfText(packet.getPrintMessage());
        BingoNet.temporaryConfig.lastChatPromptAnswer = prompt;
    }
}