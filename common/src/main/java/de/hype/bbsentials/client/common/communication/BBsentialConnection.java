package de.hype.bbsentials.client.common.communication;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.SplashManager;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.client.updatelisteners.SplashStatusUpdateListener;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.hpmodapi.HPModAPIPacket;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.InterceptPacketInfo;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.environment.packetconfig.PacketManager;
import de.hype.bbsentials.environment.packetconfig.PacketUtils;
import de.hype.bbsentials.shared.constants.*;
import de.hype.bbsentials.shared.objects.*;
import de.hype.bbsentials.shared.packets.function.*;
import de.hype.bbsentials.shared.packets.mining.ChChestPacket;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import de.hype.bbsentials.shared.packets.network.*;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;

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
import java.sql.Timestamp;
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
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! " + command + " please check if its safe. IMMEDIATELY report this to the Admins and DeveloperAbstractConfig Hype_the_Time (@hackthetime). If it is not safe immediately remove BBsentials!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelfFatal("§4" + emergencyMessage + "\n\n");
        }
        return false;
    }

    public static boolean selfDestruct() {
        try {
            // Get the path to the running JAR file
            String jarFilePath = BBsentials.class.getProtectionDomain()
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
                    InputStream resouceInputStream = BBsentials.class.getResourceAsStream("/assets/public_bbsentials_cert.crt");

                    // Check if the resource exists
                    if (resouceInputStream == null) {
                        System.out.println("The resource '/assets/public_bbsentials_cert.crt' was not found.");
                        return;
                    }

                    File tempFile = File.createTempFile("public_bbsentials_cert", ".crt");
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
                        if (BBsentials.developerConfig.isDetailedDevModeEnabled())
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
            if (BBsentials.developerConfig.isDetailedDevModeEnabled()) {
                Chat.sendPrivateMessageToSelfDebug("BBDev-s: " + message);
            }
            try {
                if (socket.isConnected() && writer != null) {
                    if (BBsentials.developerConfig.isDetailedDevModeEnabled())
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
            if (BBsentials.developerConfig.isDetailedDevModeEnabled() && !((packet.getClass().equals(RequestConnectPacket.class) && !BBsentials.bbServerConfig.useMojangAuth) && BBsentials.developerConfig.devSecurity)) {
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
        if (packet.splash.announcer.equals(BBsentials.generalConfig.getUsername()) && BBsentials.splashConfig.autoSplashStatusUpdates) {
            Chat.sendPrivateMessageToSelfInfo("The Splash Update Statuses will be updatet automatically for you. If you need to do something manually go into Discord Splash Dashboard");
            SplashStatusUpdateListener splashStatusUpdateListener = new SplashStatusUpdateListener(packet.splash);
            UpdateListenerManager.splashStatusUpdateListener = splashStatusUpdateListener;
            BBsentials.executionService.execute(splashStatusUpdateListener);
        } else {
            SplashManager.addSplash(packet);
            if (packet.splash.lessWaste) {
                waitTime = Math.min(((EnvironmentCore.utils.getPotTime() * 1000) / 80), 25 * 1000);
            } else {
                waitTime = 0;
            }
            BBsentials.executionService.schedule(() -> {
                SplashManager.display(packet.splash.splashId);
            }, waitTime, TimeUnit.MILLISECONDS);
        }
    }

    public void onBingoChatMessagePacket(BingoChatMessagePacket packet) {
        if (BBsentials.visualConfig.showBingoChat) {
            Chat.sendPrivateMessageToSelfInfo("[" + packet.prefix + "§r] " + packet.username + ": " + packet.message);
        }
    }

    public void onMiningEventPacket(MiningEventPacket packet) {
        if (BBsentials.miningEventConfig.blockChEvents && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
            return;
        if (!(BBsentials.miningEventConfig.allEvents)) {
            if (packet.event.equals(MiningEvents.RAFFLE)) {
                if (!BBsentials.miningEventConfig.raffle) return;
            } else if (packet.event.equals(MiningEvents.GOBLIN_RAID)) {
                if (!BBsentials.miningEventConfig.goblinRaid) return;
            } else if (packet.event.equals(MiningEvents.MITHRIL_GOURMAND)) {
                if (!BBsentials.miningEventConfig.mithrilGourmand) return;
            } else if (packet.event.equals(MiningEvents.BETTER_TOGETHER)) {
                if (BBsentials.miningEventConfig.betterTogether.equals("none")) return;
                if (BBsentials.miningEventConfig.betterTogether.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BBsentials.miningEventConfig.betterTogether.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            } else if (packet.event.equals(MiningEvents.DOUBLE_POWDER)) {
                if (BBsentials.miningEventConfig.doublePowder.equals("none")) return;
                if (BBsentials.miningEventConfig.doublePowder.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BBsentials.miningEventConfig.doublePowder.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            } else if (packet.event.equals(MiningEvents.GONE_WITH_THE_WIND)) {
                if (BBsentials.miningEventConfig.goneWithTheWind.equals("none")) return;
                if (BBsentials.miningEventConfig.goneWithTheWind.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BBsentials.miningEventConfig.goneWithTheWind.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            }
        }
        Chat.sendPrivateMessageToSelfImportantInfo(packet.username + ": There is a " + packet.event.getDisplayName() + " in the " + packet.island.getDisplayName() + " now/soon.");
    }


    public void onWelcomePacket(WelcomeClientPacket packet) {
        authenticated = packet.success;
        if (packet.success) {
            BBsentials.generalConfig.bbsentialsRoles = new HashSet<>(packet.roles);
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
                BBsentials.connection.close();
            } catch (Exception ignored) {
            }
            for (int i = 0; i < packet.waitBeforeReconnect.length; i++) {
                int finalI = i;
                BBsentials.executionService.schedule(() -> {
                    if (finalI == 0) {
                        BBsentials.connectToBBserver();
                    } else {
                        BBsentials.conditionalReconnectToBBserver();
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
                BBsentials.executionService.schedule(() -> Chat.sendPrivateMessageToSelfFatal("BB: Time till crash: " + finalI), i, TimeUnit.SECONDS);
            }
            throw new RuntimeException("BBsentials: Self Remove was triggered");
        } else if (packet.command.equals(InternalCommandPacket.PEACEFULLDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated! Becomes effective on next launch");
            if (!packet.parameters[0].isEmpty())
                Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.utils.playsound("block.anvil.destroy");
        } else if (packet.command.equals(InternalCommandPacket.HUB)) {
            BBsentials.sender.addImmediateSendTask("/hub");
        } else if (packet.command.equals(InternalCommandPacket.PRIVATE_ISLAND)) {
            BBsentials.sender.addImmediateSendTask("/is");
        } else if (packet.command.equals(InternalCommandPacket.HIDDEN_HUB)) {
            BBsentials.sender.addHiddenSendTask("/hub", 0);
        } else if (packet.command.equals(InternalCommandPacket.HIDDEN_PRIVATE_ISLAND)) {
            BBsentials.sender.addHiddenSendTask("/is", 0);
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
            System.out.println("BBsentials: InstaCrash triggered");
            EnvironmentCore.utils.systemExit(69);
        }
    }

    public void onInvalidCommandFeedbackPacket(InvalidCommandFeedbackPacket packet) {
        Chat.sendPrivateMessageToSelfError(packet.displayMessage);
    }

    public void onPartyPacket(PartyPacket packet) {
        if (BBsentials.partyConfig.allowServerPartyInvite) {
            ClientboundPartyInfoPacket partyInfo = HPModAPIPacket.PARTYINFO.complete();
            if (!partyInfo.isInParty() && !(packet.type == PartyConstants.JOIN || packet.type == PartyConstants.ACCEPT))
                return;
            boolean leader = partyInfo.getLeader().get().equals(BBsentials.generalConfig.getMCUUIDID());
            boolean moderator = partyInfo.getMemberMap().get(BBsentials.generalConfig.getMCUUIDID()).getRole().equals(ClientboundPartyInfoPacket.PartyRole.MOD);

            if (packet.type == PartyConstants.JOIN) {
                Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party join");
                if (partyInfo.isInParty()) BBsentials.sender.addSendTask("/p leave");
                BBsentials.sender.addSendTask("/p join " + packet.users.get(0));
            } else if (packet.type == PartyConstants.ACCEPT) {
                if (partyInfo.isInParty()) BBsentials.sender.addSendTask("/p leave");
                Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party accept");
                BBsentials.sender.addSendTask("/p accept " + packet.users.get(0));
            } else if (packet.type == PartyConstants.DISBAND) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party disband");
                    Chat.sendCommand("/p disband");
                } else {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party disband but you are not the leader. Leaving party");
                    Chat.sendCommand("/p leave");
                }
            } else if (packet.type == PartyConstants.INVITE) {
                if (leader || moderator) {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party invite");
                    List<String> users = packet.users;
                    int chunkSize = 5;
                    for (int i = 0; i < users.size(); i += chunkSize) {
                        List<String> chunk = users.subList(i, Math.min(users.size(), i + chunkSize));
                        Chat.sendCommand("/p invite " + String.join(" ", chunk));
                    }
                } else {
                    BBsentials.sender.addSendTask("/pc BBsentials Server requested a party invite for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.WARP) {
                if (leader || moderator) {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party warp");
                    Chat.sendCommand("/p warp");
                } else {
                    BBsentials.sender.addSendTask("/pc BBsentials Server requested a party warp");
                }
            } else if (packet.type == PartyConstants.KICK) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party kick");
                    packet.users.forEach((u) -> BBsentials.sender.addSendTask("/p kick %s".formatted(u)));
                } else {
                    BBsentials.sender.addSendTask("/pc BBsentials Server requested a party kicks for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.PROMOTE) {
                if (leader) {
                    Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party promote");
                    Chat.sendCommand("/p promote " + packet.users.get(0));
                } else {
                    BBsentials.sender.addSendTask("/pc BBsentials Server requested party promotion for: %s".formatted(packet.users));
                }
            } else if (packet.type == PartyConstants.LEAVE) {
                Chat.sendPrivateMessageToSelfInfo("BBsentials Server requested party leave");
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
            Chat.sendPrivateMessageToSelfSuccess("Logging into BBsentials-online (Beta Development Server)");
            Chat.sendPrivateMessageToSelfImportantInfo("You may test here but do NOT Spam unless you have very good reasons. Spamming may still be punished");
        } else {
            Chat.sendPrivateMessageToSelfSuccess("Logging into BBsentials-online");
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

        if (BBsentials.bbServerConfig.useMojangAuth) {
            EnvironmentCore.utils.mojangAuth(serverId);
            RequestConnectPacket connectPacket = new RequestConnectPacket(BBsentials.generalConfig.getMCUUID(), clientRandom, EnvironmentCore.utils.getModVersion(), EnvironmentCore.utils.getGameVersion(), BBsentials.generalConfig.getApiVersion(), AuthenticationConstants.MOJANG);
            sendPacket(connectPacket);
        } else {
            sendPacket(new RequestConnectPacket(BBsentials.generalConfig.getMCUUID(), BBsentials.bbServerConfig.apiKey, EnvironmentCore.utils.getModVersion(), EnvironmentCore.utils.getGameVersion(), BBsentials.generalConfig.getApiVersion(), AuthenticationConstants.DATABASE));
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
            if (BBsentials.bbthread != null) {
                BBsentials.bbthread.interrupt();
            }
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
            if (messageQueue != null) messageQueue.clear();
            if (BBsentials.bbthread != null) {
                BBsentials.bbthread.join();
                BBsentials.bbthread = null;
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
        if (!BBsentials.visualConfig.showCardCompletions && packet.completionType.equals(CompletedGoalPacket.CompletionType.CARD))
            Chat.sendPrivateMessageToSelfText(Message.tellraw("[\"\",{\"text\":\"@username \",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"just completed the \",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"Bingo\",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"!\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}}]".replace("@username", packet.username).replace("@lore", packet.lore)));
            //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"@username "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"just completed the "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"Bingo"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gray","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@lore"}],"text":"!"}],"command":"%s","jtemplate":"tellraw"}
        else if (!BBsentials.visualConfig.showGoalCompletions && packet.completionType.equals(CompletedGoalPacket.CompletionType.GOAL))
            Chat.sendPrivateMessageToSelfText(Message.tellraw("[\"\",{\"text\":\"@username \",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"just completed the Goal \",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"@name\",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}},{\"text\":\"!\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@lore\"]}}]".replace("@username", packet.username).replace("@lore", packet.lore).replace("@name", packet.name)));
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
        if (packet.maximumPlayerCount != null && packet.maximumPlayerCount > playerCount.size()) return;
        if (packet.minimumPlayerCount != null && packet.minimumPlayerCount < playerCount.size()) return;
        if (packet.username != null && !playerCount.contains(packet.username)) return;
        sendPacket(packet.preparePacketToReplyToThis(new WantedSearchPacket.WantedSearchPacketReply(BBsentials.generalConfig.getUsername(), EnvironmentCore.utils.getPlayers(), EnvironmentCore.utils.isOnMegaServer(), EnvironmentCore.utils.getServerId())));
    }

    public void onPunishedPacket(PunishedPacket packet) {
        for (PunishmentData data : packet.data) {
            if (!data.isActive()) continue;
            if (data.disconnectFromNetworkOnLoad) close();
            if (data.modSelfRemove) selfDestruct();
            if (!data.silentCrash) {
                Chat.sendPrivateMessageToSelfFatal("You have been " + data.pointPunishmentType + " in the BBsentials Network! Reason: " + data.reason);
                Chat.sendPrivateMessageToSelfFatal("Punishment Expiration Date: " + new Timestamp(data.till.getEpochSecond()).toLocalDateTime().toString());
                if (data.modSelfRemove)
                    Chat.sendPrivateMessageToSelfFatal("You have been disallowed to use the mod, which is the reason it is automatically self removing itself!");
            }
            if (data.shouldModCrash) {
                for (int i = 0; i < data.warningTimeBeforeCrash; i++) {
                    if (!data.silentCrash) Chat.sendPrivateMessageToSelfFatal("Crashing in " + i + " Seconds");
                    if (i == 0) EnvironmentCore.utils.systemExit(data.exitCodeOnCrash);
                }
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
        if (!BBsentials.partyConfig.allowBBinviteMe && command.trim().equalsIgnoreCase("/msg " + BBsentials.generalConfig.getUsername() + " bb:party me")) {
            Chat.sendPrivateMessageToSelfImportantInfo("Enabled bb:party invites temporarily. Will be disabled on Server swap!");
            BBsentials.partyConfig.allowBBinviteMe = true;
            ServerSwitchTask.onServerLeaveTask(() -> BBsentials.partyConfig.allowBBinviteMe = false);
        } else if (command.trim().equalsIgnoreCase("/p join " + BBsentials.generalConfig.getUsername())) {
            if (!BBsentials.partyConfig.isPartyLeader) BBsentials.sender.addImmediateSendTask("/p leave");
            BBsentials.sender.addHiddenSendTask("/stream open 23", 1);
            BBsentials.sender.addHiddenSendTask("/pl", 2);
            Chat.sendPrivateMessageToSelfImportantInfo("Opened Stream Party for you since you announced chchest items");
        }

        BBsentials.connection.sendPacket(new ChChestPacket(new ChestLobbyData(List.of(new ChChestData("", coords, items)), EnvironmentCore.utils.getServerId(), command, extraMessage, StatusConstants.OPEN)));
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
                BBsentials.sender.addSendTask(command.command, command.delay);
            }
        }, 10);
        Chat.sendPrivateMessageToSelfText(packet.getPrintMessage());
        BBsentials.temporaryConfig.lastChatPromptAnswer = prompt;
    }

    public void onPacketChatPromptPacket(PacketChatPromptPacket packet) {
        ChatPrompt prompt = new ChatPrompt(() -> {
            for (AbstractPacket p : packet.getPackets()) {
                sendPacket(p);
            }
        }, 10);
        Chat.sendPrivateMessageToSelfText(packet.getPrintMessage());
        BBsentials.temporaryConfig.lastChatPromptAnswer = prompt;
    }
}