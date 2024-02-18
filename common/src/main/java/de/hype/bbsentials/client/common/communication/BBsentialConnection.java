package de.hype.bbsentials.client.common.communication;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.SplashManager;
import de.hype.bbsentials.client.common.client.updatelisteners.SplashStatusUpdateListener;
import de.hype.bbsentials.client.common.client.updatelisteners.UpdateListenerManager;
import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.environment.packetconfig.PacketManager;
import de.hype.bbsentials.environment.packetconfig.PacketUtils;
import de.hype.bbsentials.shared.constants.*;
import de.hype.bbsentials.shared.objects.ClientWaypointData;
import de.hype.bbsentials.shared.objects.SplashData;
import de.hype.bbsentials.shared.packets.function.*;
import de.hype.bbsentials.shared.packets.mining.ChChestPacket;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import de.hype.bbsentials.shared.packets.network.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class BBsentialConnection {
    public Thread messageReceiverThread;
    public Thread messageSenderThread;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private LinkedBlockingQueue<String> messageQueue;
    private PacketManager packetManager;

    public BBsentialConnection() {
        packetManager = new PacketManager(this);
    }


    public static boolean isCommandSafe(String command) {
        if (command.startsWith("/p ") || command.startsWith("/party ") || command.startsWith("/boop ") || command.startsWith("/msg ") || command.startsWith("/hub ")) {
            return true;
        }
        else {
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! " + command + " please check if its safe. IMMEDIATELY report this to the Admins and DeveloperAbstractConfig Hype_the_Time (@hackthetime). If it is not safe immediately remove BBsentials!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelfFatal("§4" + emergencyMessage + "\n\n");
        }
        return false;
    }


    public void connect(String serverIP, int serverPort) {
        // Enable SSL handshake debugging
        System.setProperty("javax.net.debug", "ssl,handshake");
        FileInputStream inputStream = null;
        try {
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
                    }
                    else {
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
                        }
                        else {
                            Chat.sendPrivateMessageToSelfError("BB: It seemed like you disconnected.");
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
            e.printStackTrace();
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        if (messageQueue != null) {
            Chat.sendPrivateMessageToSelfDebug("BBs: " + message);
            messageQueue.offer(message);
        }
        else {
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
            }
            else {
                Chat.sendPrivateMessageToSelfSuccess("BB: " + message);
            }
        }
    }

    public <T extends AbstractPacket> void dummy(T o) {
        //this does absolutely nothing. dummy for packet in packt manager
    }

    public void splashHighlightItem(SplashData splash, long displayTimeInMilliseconds) {
        CustomItemTexture toHighlightHub = new CustomItemTexture("customitems/splash_hub") {
            @Override
            public boolean isItem(String itemName, String tooltip, String nbt, Object item) {
                return itemName.endsWith("Hub #" + splash.hubNumber);
            }
        };
        BBsentials.executionService.schedule(() -> {
            toHighlightHub.removeFromPool();
        }, displayTimeInMilliseconds, TimeUnit.MILLISECONDS);
    }


    public <E extends AbstractPacket> void sendPacket(E packet) {
        String packetName = packet.getClass().getSimpleName();
        String rawjson = PacketUtils.parsePacketToJson(packet);
        if (isConnected() && writer != null) {
            if (BBsentials.developerConfig.isDetailedDevModeEnabled() && !((packet.getClass().equals(RequestConnectPacket.class) && !BBsentials.bbServerConfig.useMojangAuth) && BBsentials.developerConfig.devSecurity)) {
                Chat.sendPrivateMessageToSelfDebug("BBDev-sP: " + packetName + ": " + rawjson);
            }
            writer.println(packetName + "." + rawjson);
        }
        else {
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
        }
        else {
            SplashManager.addSplash(packet);
            if (packet.splash.lessWaste) {
                waitTime = Math.min(((EnvironmentCore.utils.getPotTime() * 1000) / 80), 25 * 1000);
            }
            else {
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

    public void onChChestPacket(ChChestPacket packet) {
        UpdateListenerManager.registerChest(packet.lobby);
        if (isCommandSafe(packet.lobby.bbcommand)) {
            if (showChChest(packet.lobby.chests.get(0).items)) {
                String tellrawText = ("{\"text\":\"BB: @username found @item in a chest at (@coords). Click here to get a party invite @extramessage\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}");
                tellrawText = tellrawText.replace("@username", packet.lobby.contactMan);
                tellrawText = tellrawText.replace("@item", Arrays.stream(packet.lobby.chests.get(0).items)
                        .map(ChChestItem::getDisplayName)
                        .collect(Collectors.toList())
                        .toString());
                tellrawText = tellrawText.replace("@coords", packet.lobby.chests.get(0).coords.toString());
                tellrawText = tellrawText.replace("@inviteCommand", packet.lobby.bbcommand);
                if (!(packet.lobby.extraMessage == null || packet.lobby.extraMessage.isEmpty())) {
                    tellrawText = tellrawText.replace("@extramessage", " : " + packet.lobby.extraMessage);
                }
                Chat.sendPrivateMessageToSelfText(Message.tellraw(tellrawText));
            }
        }
        else {
            Chat.sendPrivateMessageToSelfFatal("Potentially dangerous packet detected: " + PacketUtils.parsePacketToJson(packet));
        }
    }

    public void onMiningEventPacket(MiningEventPacket packet) {
        if (BBsentials.miningEventConfig.blockChEvents && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
            return;
        if (!(BBsentials.miningEventConfig.allEvents)) {
            if (packet.event.equals(MiningEvents.RAFFLE)) {
                if (!BBsentials.miningEventConfig.raffle) return;
            }
            else if (packet.event.equals(MiningEvents.GOBLIN_RAID)) {
                if (!BBsentials.miningEventConfig.goblinRaid) return;
            }
            else if (packet.event.equals(MiningEvents.MITHRIL_GOURMAND)) {
                if (!BBsentials.miningEventConfig.mithrilGourmand) return;
            }
            else if (packet.event.equals(MiningEvents.BETTER_TOGETHER)) {
                if (BBsentials.miningEventConfig.betterTogether.equals("none")) return;
                if (BBsentials.miningEventConfig.betterTogether.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BBsentials.miningEventConfig.betterTogether.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            }
            else if (packet.event.equals(MiningEvents.DOUBLE_POWDER)) {
                if (BBsentials.miningEventConfig.doublePowder.equals("none")) return;
                if (BBsentials.miningEventConfig.doublePowder.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                    return;
                if (BBsentials.miningEventConfig.doublePowder.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                    return;
            }
            else if (packet.event.equals(MiningEvents.GONE_WITH_THE_WIND)) {
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
        if (packet.success) {
            BBsentials.generalConfig.bbsentialsRoles = packet.roles;
            Chat.sendPrivateMessageToSelfSuccess("Login Success");
            if (!packet.motd.isEmpty()) {
                Chat.sendPrivateMessageToSelfImportantInfo(packet.motd);
            }
        }
        else {
            Chat.sendPrivateMessageToSelfError("Login Failed");
        }
    }

    public void onDisconnectPacket(DisconnectPacket packet) {
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
                }
                else {
                    BBsentials.conditionalReconnectToBBserver();
                }
            }, (long) (packet.waitBeforeReconnect[i] + (Math.random() * packet.randomExtraDelay)), TimeUnit.SECONDS);
        }
    }

    public void onDisplayTellrawMessagePacket(DisplayTellrawMessagePacket packet) {
        /*Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(packet.message));*/
        Chat.sendPrivateMessageToSelfImportantInfo("You received a tellraw Packet but it got ignored due too there being no safety checks in this version.");
    }

    public void onInternalCommandPacket(InternalCommandPacket packet) {
        if (packet.command.equals(InternalCommandPacket.REQUEST_POT_DURATION)) {
            sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_POT_DURATION, new String[]{String.valueOf(EnvironmentCore.utils.getPotTime())}));
        }
        else if (packet.command.equals(InternalCommandPacket.SELFDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated. Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty()) Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.utils.playsound("block.anvil.destroy");
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                BBsentials.executionService.schedule(() -> Chat.sendPrivateMessageToSelfFatal("BB: Time till crash: " + finalI), i, TimeUnit.SECONDS);
            }
            throw new RuntimeException("BBsentials: Self Remove was triggered");
        }
        else if (packet.command.equals(InternalCommandPacket.PEACEFULLDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated! Becomes effective on next launch");
            if (!packet.parameters[0].isEmpty()) Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.utils.playsound("block.anvil.destroy");
        }
        else if (packet.command.equals(InternalCommandPacket.HUB)) {
            BBsentials.sender.addImmediateSendTask("/hub");
        }
        else if (packet.command.equals(InternalCommandPacket.PRIVATE_ISLAND)) {
            BBsentials.sender.addImmediateSendTask("/is");
        }
        else if (packet.command.equals(InternalCommandPacket.HIDDEN_HUB)) {
            BBsentials.sender.addHiddenSendTask("/hub", 0);
        }
        else if (packet.command.equals(InternalCommandPacket.HIDDEN_PRIVATE_ISLAND)) {
            BBsentials.sender.addHiddenSendTask("/is", 0);
        }
        else if (packet.command.equals(InternalCommandPacket.CRASH)) {
            Chat.sendPrivateMessageToSelfFatal("BB: Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty()) Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
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
        }
        else if (packet.command.equals(InternalCommandPacket.INSTACRASH)) {
            System.out.println("BBsentials: InstaCrash triggered");
            EnvironmentCore.utils.systemExit(69);
        }
    }

    public void onInvalidCommandFeedbackPacket(InvalidCommandFeedbackPacket packet) {
        Chat.sendPrivateMessageToSelfError(packet.displayMessage);
    }

    public void onPartyPacket(PartyPacket packet) {
        if (BBsentials.partyConfig.allowServerPartyInvite) {
            if (packet.type.equals(PartyConstants.DISBAND)) Chat.sendCommand("/p disband");
            else Chat.sendCommand("/p " + packet.type.toString().toLowerCase() + " " + String.join(" ", packet.users));
        }
        else {
            Chat.sendPrivateMessageToSelfImportantInfo("Blocked a Party Command from the Server: " + packet.type + " : " + String.join(" ", packet.users));
        }
    }

    public void onSystemMessagePacket(SystemMessagePacket packet) {
        if (packet.important) {
            Chat.sendPrivateMessageToSelfImportantInfo("§n" + packet.message);
        }
        else {
            Chat.sendPrivateMessageToSelfInfo(packet.message);
        }
        if (packet.ping) {
            EnvironmentCore.utils.playsound("block.anvil.destroy");
        }
    }

    public void onRequestAuthentication(RequestAuthentication packet) {
        Chat.sendPrivateMessageToSelfSuccess("Logging into BBsentials-online");
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
            RequestConnectPacket connectPacket = new RequestConnectPacket(BBsentials.generalConfig.getMCUUID(), clientRandom, BBsentials.generalConfig.getApiVersion(), AuthenticationConstants.MOJANG);
            sendPacket(connectPacket);
        }
        else {
            sendPacket(new RequestConnectPacket(BBsentials.generalConfig.getMCUUID(), BBsentials.bbServerConfig.apiKey, BBsentials.generalConfig.getApiVersion(), AuthenticationConstants.DATABASE));
        }
    }

    public boolean showChChest(ChChestItem[] items) {
        if (BBsentials.chChestConfig.allChChestItem) return true;
        for (ChChestItem item : items) {
            if (BBsentials.chChestConfig.customChChestItem && item.isCustom()) return true;
            if (BBsentials.chChestConfig.allRoboPart && item.isRoboPart()) return true;
            if (BBsentials.chChestConfig.prehistoricEgg && item.equals(ChChestItems.PrehistoricEgg))
                return true;
            if (BBsentials.chChestConfig.pickonimbus2000 && item.equals(ChChestItems.Pickonimbus2000))
                return true;
            if (BBsentials.chChestConfig.controlSwitch && item.equals(ChChestItems.ControlSwitch)) return true;
            if (BBsentials.chChestConfig.electronTransmitter && item.equals(ChChestItems.ElectronTransmitter))
                return true;
            if (BBsentials.chChestConfig.robotronReflector && item.equals(ChChestItems.RobotronReflector))
                return true;
            if (BBsentials.chChestConfig.superliteMotor && item.equals(ChChestItems.SuperliteMotor))
                return true;
            if (BBsentials.chChestConfig.syntheticHeart && item.equals(ChChestItems.SyntheticHeart))
                return true;
            if (BBsentials.chChestConfig.flawlessGemstone && item.equals(ChChestItems.FlawlessGemstone))
                return true;
            if (BBsentials.chChestConfig.jungleHeart && item.equals(ChChestItems.JungleHeart)) return true;
        }
        return false;
    }

    public boolean isConnected() {
        try {
            return socket.isConnected() && !socket.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean selfDestruct() {
        try {
            // Get the path to the running JAR file
            String jarFilePath = this.getClass().getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();

            // Create a File object for the JAR file
            File jarFile = new File(jarFilePath);

            // Check if the JAR file exists
            if (jarFile.exists()) {
                // Delete the JAR file
                return jarFile.delete();
            }
            else {
                return false;
            }
        } catch (Exception ignored) {
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
            writer.close();
            reader.close();
            socket.close();
            messageQueue.clear();
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
        }
        else if (packet.operation.equals(WaypointPacket.Operation.REMOVE)) {
            try {
                Waypoints.waypoints.get(packet.waypointId).removeFromPool();
            } catch (Exception ignored) {

            }
        }
        else if (packet.operation.equals(WaypointPacket.Operation.EDIT)) {
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
            return;
        if (!BBsentials.visualConfig.showGoalCompletions && packet.completionType.equals(CompletedGoalPacket.CompletionType.GOAL))
            return;
    }

    public void onPlaySoundPacket(PlaySoundPacket packet) {
        if (packet.streamFromUrl) EnvironmentCore.utils.streamCustomSound(packet.id, packet.durationInSeconds);
        else EnvironmentCore.utils.playsound(packet.id);
    }

    public interface MessageReceivedCallback {
        void onMessageReceived(String message);
    }
}