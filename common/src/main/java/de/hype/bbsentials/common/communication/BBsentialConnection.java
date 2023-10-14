package de.hype.bbsentials.common.communication;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.client.SplashManager;
import de.hype.bbsentials.common.client.SplashStatusUpdateListener;
import de.hype.bbsentials.common.constants.enviromentShared.*;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.packets.PacketManager;
import de.hype.bbsentials.common.packets.PacketUtils;
import de.hype.bbsentials.common.packets.packets.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BBsentialConnection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private LinkedBlockingQueue<String> messageQueue;
    private MessageReceivedCallback messageReceivedCallback;
    private String itemName = "Hub #0";
    private PacketManager packetManager;

    public BBsentialConnection() {
        packetManager = new PacketManager(this);
    }



    public static boolean isCommandSafe(String command) {
        if (command.startsWith("/p ") || command.startsWith("/party ") || command.startsWith("/boop ") || command.startsWith("/msg ") || command.startsWith("/hub ")) {
            return true;
        }
        else {
            BBsentials.connection.sendCommand("?emergency server-hacked? chchest command " + command);
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! " + command + " please check if its safe. IMMEDIATELY report this to the Admins and Developer Hype_the_Time (@hackthetime). If it is not safe immediately remove BBsentials!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelfFatal("§4" + emergencyMessage + "\n\n");
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(emergencyMessage);*/
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
            Thread messageReceiverThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = reader.readLine();
                        if (message != null) {
                            if (messageReceivedCallback != null) {
                                messageReceivedCallback.onMessageReceived(message);
                            }
                            else {
                                Chat.sendPrivateMessageToSelfError("BB: It seemed like you disconnected. Reconnecting...");
                                BBsentials.connectToBBserver();
                                try {
                                    Thread.sleep(1000 * 10);
                                } catch (Exception ignored) {
                                }
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
            Thread messageSenderThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = messageQueue.take();
                        writer.println(message);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException ignored) {
                }
            });
            messageSenderThread.start();
            messageSenderThread.setName("bb sender thread");

        } catch (IOException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            e.printStackTrace();
        } catch (
                CertificateException e) {
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
        if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
            Chat.sendPrivateMessageToSelfDebug("BBDev-s: " + message);
        }
        try {
            if (socket.isConnected() && writer != null) {
                writer.println(message);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public void sendCommand(String message) {
        if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
            Chat.sendPrivateMessageToSelfDebug("BBDev-s: " + message);
        }
        if (socket.isConnected() && writer != null) {
            writer.println(message);
        }
        else {
            Chat.sendPrivateMessageToSelfFatal("BB: It seems like the connection was lost. Please try to reconnect with /bbi reconnect");
        }
    }

    //The following onMessageReceived may or may not be modified
    // or taken out of order in private/ non official versions of the mod!
    public void onMessageReceived(String message) {
        if (!PacketUtils.handleIfPacket(this, message)) {
            if (message.startsWith("H-")) {
                if (message.equals("H-BB-Login: ")) {
                    Chat.sendPrivateMessageToSelfSuccess("Logging into BBsentials-online");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendPacket(new RequestConnectPacket(BBsentials.config.getMCUUID(), BBsentials.getConfig().getApiKey(), BBsentials.getConfig().getApiVersion(), AuthenticationConstants.DATABASE));
                }
            }
            else {
                Chat.sendPrivateMessageToSelfSuccess("BB: " + message);
            }
        }
    }

    public void dummy(Object o) {
        //this does absoloutely nothing
    }

    public void splashHighlightItem(String itemName, long displayTimeInMilliseconds) {
        this.itemName = itemName;
        BBsentials.config.highlightitem = true;
        BBsentials.executionService.schedule(() -> {
            BBsentials.config.highlightitem = false;
            try {
                socket.setSoTimeout(0);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }, displayTimeInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public String getItemName() {
        return itemName;
    }
    //TODO search

    public void setMessageReceivedCallback(MessageReceivedCallback callback) {
        this.messageReceivedCallback = callback;
    }

    public <E extends AbstractPacket> void sendPacket(E packet) {
        String packetName = packet.getClass().getSimpleName();
        String rawjson = PacketUtils.parsePacketToJson(packet);
        if (BBsentials.getConfig().isDetailedDevModeEnabled() && !(packet.getClass().equals(RequestConnectPacket.class))) {
            Chat.sendPrivateMessageToSelfDebug("BBDev-sP: " + packetName + ": " + rawjson);
        }
        if (socket.isConnected() && writer != null) {
            writer.println(packetName + "." + rawjson);
        }
        else {
            Chat.sendPrivateMessageToSelfError("BB: Couldn't send a Packet? did you get disconnected?");
        }
    }

    public void onBroadcastMessagePacket(BroadcastMessagePacket packet) {
        Chat.sendPrivateMessageToSelfImportantInfo("[A] §r[" + packet.prefix + "§r]§6 " + packet.username + ": " + packet.message);
    }

    public void onSplashNotifyPacket(SplashNotifyPacket packet) {
        int waitTime;
        if (packet.splasherUsername.equals(BBsentials.config.getUsername())&& BBsentials.config.autoSplashStatusUpdates) {
            Chat.sendPrivateMessageToSelfInfo("The Splash Update Statuses will be updatet automatically for you. If you need to do something manually go into Discord Splash Dashboard");
            SplashStatusUpdateListener splashStatusUpdateListener = new SplashStatusUpdateListener(this, packet);
            BBsentials.splashStatusUpdateListener = splashStatusUpdateListener;
            BBsentials.executionService.execute(splashStatusUpdateListener);
        }
        else {
            SplashManager.addSplash(packet);
            if (packet.lessWaste) {
                waitTime = Math.min(((EnvironmentCore.mcUtils.getPotTime() * 1000) / 80), 25 * 1000);
            }
            else {
                waitTime = 0;
            }
            BBsentials.executionService.schedule(() -> {
                SplashManager.display(packet.splashId);
            }, waitTime, TimeUnit.MILLISECONDS);
        }
    }

    public void onBingoChatMessagePacket(BingoChatMessagePacket packet) {
        if (BBsentials.config.showBingoChat) {
            Chat.sendPrivateMessageToSelfInfo("[" + packet.prefix + "] " + packet.username + ": " + packet.message);
        }
    }

    public void onChChestPacket(ChChestPacket packet) {
        if (isCommandSafe(packet.bbcommand)) {
            if (showChChest(packet.items)) {
                String tellrawText = ("{\"text\":\"BB: @username found @item in a chest at (@coords). Click here to get a party invite @extramessage\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}");
                tellrawText = tellrawText.replace("@username", packet.announcerUsername);
                tellrawText = tellrawText.replace("@item", Arrays.stream(packet.items)
                        .map(ChChestItem::getDisplayName)
                        .collect(Collectors.toList())
                        .toString());
                tellrawText = tellrawText.replace("@coords", packet.locationCoords);
                tellrawText = tellrawText.replace("@inviteCommand", packet.bbcommand);
                if (!(packet.extraMessage == null || packet.extraMessage.isEmpty())) {
                    tellrawText = tellrawText.replace("@extramessage", " : " + packet.extraMessage);
                }
                Chat.sendPrivateMessageToSelfText(new Message(tellrawText,""));
            }
        }
        else {
            Chat.sendPrivateMessageToSelfImportantInfo("§cFiltered out a suspicious packet! Please send a screenshot of this message with ping Hype_the_Time (hackthetime) in Bingo Apothecary, BB, SBZ, offical Hypixel,...");
            Chat.sendPrivateMessageToSelfImportantInfo(PacketUtils.parsePacketToJson(packet));
        }
    }

    public void onMiningEventPacket(MiningEventPacket packet) {
        if (!BBsentials.config.toDisplayConfig.getValue("disableAll")) {
            //its will returns false cause disabled is checked already before.
            if (BBsentials.config.toDisplayConfig.blockChEvents && packet.island.equals(Islands.CRYSTAL_HOLLOWS)) return;
            if (!(BBsentials.config.toDisplayConfig.allEvents)) {
                if (packet.event.equals(MiningEvents.RAFFLE)) {
                    if (!BBsentials.config.toDisplayConfig.raffle) return;
                }
                else if (packet.event.equals(MiningEvents.GOBLIN_RAID)) {
                    if (!BBsentials.config.toDisplayConfig.goblinRaid) return;
                }
                else if (packet.event.equals(MiningEvents.MITHRIL_GOURMAND)) {
                    if (!BBsentials.config.toDisplayConfig.mithrilGourmand) return;
                }
                else if (packet.event.equals(MiningEvents.BETTER_TOGETHER)) {
                    if (BBsentials.config.toDisplayConfig.betterTogether.equals("none")) return;
                    if (BBsentials.config.toDisplayConfig.betterTogether.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                        return;
                    if (BBsentials.config.toDisplayConfig.betterTogether.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                        return;
                }
                else if (packet.event.equals(MiningEvents.DOUBLE_POWDER)) {
                    if (BBsentials.config.toDisplayConfig.doublePowder.equals("none")) return;
                    if (BBsentials.config.toDisplayConfig.doublePowder.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                        return;
                    if (BBsentials.config.toDisplayConfig.doublePowder.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                        return;
                }
                else if (packet.event.equals(MiningEvents.GONE_WITH_THE_WIND)) {
                    if (BBsentials.config.toDisplayConfig.goneWithTheWind.equals("none")) return;
                    if (BBsentials.config.toDisplayConfig.goneWithTheWind.equals(Islands.DWARVEN_MINES.getDisplayName()) && packet.island.equals(Islands.CRYSTAL_HOLLOWS))
                        return;
                    if (BBsentials.config.toDisplayConfig.goneWithTheWind.equals(Islands.CRYSTAL_HOLLOWS.getDisplayName()) && packet.island.equals(Islands.DWARVEN_MINES))
                        return;
                }
            }
            Chat.sendPrivateMessageToSelfImportantInfo(packet.username + "There is a " + packet.event.getDisplayName() + "in the " + packet.island.getDisplayName() + " now/soon.");
        }
    }

    public void onWelcomePacket(WelcomeClientPacket packet) {
        if (packet.success) {
            BBsentials.config.bbsentialsRoles = packet.roles;
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
        BBsentials.connection = null;
        for (int i = 0; i < packet.waitBeforeReconnect.length; i++) {
            int finalI = i;
            BBsentials.executionService.schedule(() -> {
                if (finalI == 1) {
                    BBsentials.connectToBBserver();
                }
                else {
                    BBsentials.conditionalReconnectToBBserver();
                }
            }, (long) (packet.waitBeforeReconnect[i] + (Math.random() * packet.randomExtraDelay)), TimeUnit.SECONDS);
        }
    }

    public void onDisplayTellrawMessagePacket(DisplayTellrawMessagePacket packet) {
        /*Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(packet.rawJson));*/
        Chat.sendPrivateMessageToSelfImportantInfo("You received a tellraw Packet but it got ignored due too there being no safety checks in this version.");
    }

    public void onInternalCommandPacket(InternalCommandPacket packet) {
        if (packet.command.equals(InternalCommandPacket.REQUEST_POT_DURATION)) {
            sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_POT_DURATION, new String[]{String.valueOf(EnvironmentCore.mcUtils.getPotTime())}));
        }
        else if (packet.command.equals(InternalCommandPacket.SELFDESTRUCT)) {
            selfDestruct();
            Chat.sendPrivateMessageToSelfFatal("BB: Self remove activated. Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty()) Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            EnvironmentCore.mcUtils.playsound("block.anvil.destroy");
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
            EnvironmentCore.mcUtils.playsound("block.anvil.destroy");
        }
        else if (packet.command.equals(InternalCommandPacket.HUB)) {
            BBsentials.config.sender.addImmediateSendTask("/hub");
        }
        else if (packet.command.equals(InternalCommandPacket.PRIVATE_ISLAND)) {
            BBsentials.config.sender.addImmediateSendTask("/is");
        }
        else if (packet.command.equals(InternalCommandPacket.HIDDEN_HUB)) {
            BBsentials.config.sender.addHiddenSendTask("/hub", 0);
        }
        else if (packet.command.equals(InternalCommandPacket.HIDDEN_PRIVATE_ISLAND)) {
            BBsentials.config.sender.addHiddenSendTask("/is", 0);
        }
        else if (packet.command.equals(InternalCommandPacket.CRASH)) {
            Chat.sendPrivateMessageToSelfFatal("BB: Stopping in 10 seconds.");
            if (!packet.parameters[0].isEmpty()) Chat.sendPrivateMessageToSelfFatal("Reason: " + packet.parameters[0]);
            Thread crashThread = new Thread(() -> {
                EnvironmentCore.mcUtils.playsound("block.anvil.destroy");
                for (int i = 10; i >= 0; i--) {
                    Chat.sendPrivateMessageToSelfFatal("BB: Time till crash: " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
                System.exit(69);
            });
            crashThread.start();
        }
        else if (packet.command.equals(InternalCommandPacket.INSTACRASH)) {
            System.out.println("BBsentials: InstaCrash triggered");
            System.exit(69);
        }
    }

    public void onInvalidCommandFeedbackPacket(InvalidCommandFeedbackPacket packet) {
        Chat.sendPrivateMessageToSelfError(packet.displayMessage);
    }

    public void onPartyPacket(PartyPacket packet) {
        if (BBsentials.config.allowServerPartyInvite) {
            Chat.sendCommand("/p " + packet.type + String.join(" ", packet.users));
        }
        else {
          Chat.sendPrivateMessageToSelfImportantInfo("Blocked a Party Command from the Server: "+packet.type+" : "+String.join(" ", packet.users));
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
            EnvironmentCore.mcUtils.playsound("block.anvil.destroy");
        }
    }

    public boolean showChChest(ChChestItem[] items) {
        if (BBsentials.config.toDisplayConfig.allChChestItem) return true;
        for (ChChestItem item : items) {
            if (BBsentials.config.toDisplayConfig.customChChestItem && item.isCustom()) return true;
            if (BBsentials.config.toDisplayConfig.allRoboPart && item.isRoboPart()) return true;
            if (BBsentials.config.toDisplayConfig.prehistoricEgg && item.equals(ChChestItems.PrehistoricEgg)) return true;
            if (BBsentials.config.toDisplayConfig.pickonimbus2000 && item.equals(ChChestItems.Pickonimbus2000)) return true;
            if (BBsentials.config.toDisplayConfig.controlSwitch && item.equals(ChChestItems.ControlSwitch)) return true;
            if (BBsentials.config.toDisplayConfig.electronTransmitter && item.equals(ChChestItems.ElectronTransmitter))
                return true;
            if (BBsentials.config.toDisplayConfig.robotronReflector && item.equals(ChChestItems.RobotronReflector)) return true;
            if (BBsentials.config.toDisplayConfig.superliteMotor && item.equals(ChChestItems.SuperliteMotor)) return true;
            if (BBsentials.config.toDisplayConfig.syntheticHeart && item.equals(ChChestItems.SyntheticHeart)) return true;
            if (BBsentials.config.toDisplayConfig.flawlessGemstone && item.equals(ChChestItems.FlawlessGemstone)) return true;
            if (BBsentials.config.toDisplayConfig.jungleHeart && item.equals(ChChestItems.JungleHeart)) return true;
        }
        return false;
    }

    public boolean isConnected() {
        try {
            socket.isConnected();
            return true;
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

    public interface MessageReceivedCallback {
        void onMessageReceived(String message);
    }
}