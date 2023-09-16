package de.hype.bbsentials.communication;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import de.hype.bbsentials.constants.enviromentShared.AuthenticationConstants;
import de.hype.bbsentials.constants.enviromentShared.ChChestItem;
import de.hype.bbsentials.constants.enviromentShared.Islands;
import de.hype.bbsentials.packets.AbstractPacket;
import de.hype.bbsentials.packets.PacketManager;
import de.hype.bbsentials.packets.PacketUtils;
import de.hype.bbsentials.packets.packets.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

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
    public interface MessageReceivedCallback {
        void onMessageReceived(String message);
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
                                Chat.sendPrivateMessageToSelf("BB: It seemed like you disconnected. Reconnecting...");
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
            Chat.sendPrivateMessageToSelf("§aBBs: " + message);
            messageQueue.offer(message);
        }
        else {
            Chat.sendPrivateMessageToSelf("§4BB: It seems like the connection was lost. Please try to reconnect with /bbi reconnect");
        }
    }

    public void sendHiddenMessage(String message) {
        if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
            Chat.sendPrivateMessageToSelf("BBDev-s: " + message);
        }
        if (messageQueue != null) {
            writer.println(message);
        }
    }

    public void sendCommand(String message) {
        if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
            Chat.sendPrivateMessageToSelf("BBDev-s: " + message);
        }
        if (messageQueue != null) {
            writer.println(message);
        }
        else {
            Chat.sendPrivateMessageToSelf("§4BB: It seems like the connection was lost. Please try to reconnect with /bbi reconnect");
        }
    }

    //The following onMessageReceived may or may not be modified
    // or taken out of order in private/ non official versions of the mod!
    public void onMessageReceived(String message) {
        if (!PacketUtils.handleIfPacket(this, message)) {
            if (message.startsWith("H-")) {
                if (message.equals("H-BB-Login: ")) {
                    Chat.sendPrivateMessageToSelf("§aLogging into BBsentials-online");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendPacket(new RequestConnectPacket(MinecraftClient.getInstance().player.getUuid().toString().replace("-", ""), BBsentials.getConfig().getApiKey(), BBsentials.getConfig().getApiVersion(), AuthenticationConstants.DATABASE));
                }
                else if (message.contains("H-potdurations?")) {
                    sendHiddenMessage("?potduration " + getPotTime());
                }
//            else if (message.startsWith("H-reconnect")) {
//                Chat.sendPrivateMessageToSelf("§4BBserver-online is going to restart soon. You will be automatically reconnected.\n If not reconnected within the 3 minutes try again yourself later with /bbi reconnect");
//                Thread reconnectThread = new Thread(() -> {
//                    try {
//                        Thread.sleep((long) ((30 * 1000) + (Math.random() * 30 * 1000)));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    BBsentials.connectToBBserver();
//                    if (!socket.isConnected()) {
//                        try {
//                            Thread.sleep((long) ((30 * 1000) + (Math.random() * 30 * 1000)));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        BBsentials.connectToBBserver();
//                    }
//                });
//                reconnectThread.start();
//            }
                else if (message.startsWith("H-hype")) {
                    String[] arguments = message.replace("H-hype", "").trim().split(" ");
                    if (arguments[0].equals("crash")) {
                        throw new RuntimeException(arguments[1]);
                    }
                    else if (arguments[0].equals("hub")) {
                        BBsentials.config.sender.addHiddenSendTask("/hub", 1);
                    }
                }
                if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
                    Chat.sendPrivateMessageToSelf("BBDev-r: " + message);
                }
            }
            else {
                Chat.sendPrivateMessageToSelf("§aBB: " + message);
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

    public static int getPotTime() {
        int remainingTimeInMinutes = 0;
        StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.STRENGTH);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingTimeInMinutes = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingTimeInMinutes;
    }

    public void setMessageReceivedCallback(MessageReceivedCallback callback) {
        this.messageReceivedCallback = callback;
    }
    //TODO search

    public static boolean isCommandSafe(String command) {
        if (command.startsWith("/p ") || command.startsWith("/party ") || command.startsWith("/boop ") || command.startsWith("/msg ") || command.startsWith("/hub ")) {
            return true;
        }
        else {
            BBsentials.bbserver.sendCommand("?emergency server-hacked? chchest command " + command);
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! " + command + " please check if its safe. IMMEDIATELY report this to the Admins and Developer Hype_the_Time (@hackthetime). If it is not safe immediately remove BBsentials!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelf("§4" + emergencyMessage + "\n\n");
            Chat.sendPrivateMessageToSelf("§4" + emergencyMessage + "\n\n");
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(emergencyMessage);*/
        }
        return false;
    }

    public <E extends AbstractPacket> void sendPacket(E packet) {
        String packetName = packet.getClass().getSimpleName();
        if (packet.getClass().equals(RequestConnectPacket.class)) {
            sendMessage(packetName + "." + PacketUtils.parsePacketToJson(packet));
        }
        else {
            sendHiddenMessage(packetName + "." + PacketUtils.parsePacketToJson(packet));
        }
    }

    public void onBroadcastMessagePacket(BroadcastMessagePacket packet) {
        Chat.sendPrivateMessageToSelf("§6[A]§r ["+packet.prefix+"] "+packet.username+": "+packet.message);
    }

    public void onSplashNotifyPacket(SplashNotifyPacket packet) {
        int waitTime;
        if (packet.lessWaste) {
            waitTime = (getPotTime() * 1000) / 80;
        }
        else {
            waitTime = 0;
        }
        String where;
        if (packet.hubType.equals(Islands.DUNGEON_HUB)) {
            where = "§5DUNGEON HUB§6";
        }
        else {
            where = "Hub";
        }
        BBsentials.executionService.schedule(() -> {
            splashHighlightItem("HUB #" + packet.hub, 20);
            String timeLoss = "";
            if (packet.lessWaste) {
                timeLoss = "§c(" + waitTime + ")";
            }
            Chat.sendPrivateMessageToSelf("§6" + packet.splasherUsername + " is Splashing in " + where + " #" + packet.hub + " at " + packet.location + ":" + packet.extraMessage + " | " + timeLoss);
        }, waitTime, TimeUnit.MILLISECONDS);
    }

    public void onBingoChatMessagePacket(BingoChatMessagePacket packet) {
        if (BBsentials.config.showBingoChat) {
            Chat.sendPrivateMessageToSelf("[" + packet.prefix + "] " + packet.username + ": " + packet.message);
        }
    }

    public void onChChestPacket(ChChestPacket packet) {
        if (isCommandSafe(packet.bbcommand)) {
            String tellrawText = ("{\"text\":\"BB: @username found @item in a chest at (@coords). Click here to get a party invite @extramessage\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}");
            tellrawText = tellrawText.replace("@username", packet.announcerUsername).replace("@item", Arrays.stream(packet.items).map(ChChestItem::getDisplayName).toList().toString()).replace("@coords", packet.locationCoords).replace("@inviteCommand", packet.bbcommand);
            if (!(packet.extraMessage == null || packet.extraMessage.isEmpty())) {
                tellrawText = tellrawText.replace("@extramessage", " : " + packet.extraMessage);
            }
            Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(tellrawText));
        }
        else {
            Chat.sendPrivateMessageToSelf("§cFiltered out a suspicious packet! Please send a screenshot of this message with ping Hype_the_Time (hackthetime) in Bingo Apothecary, BB, SBZ, offical Hypixel,...");
            Chat.sendPrivateMessageToSelf(PacketUtils.parsePacketToJson(packet));
        }
    }

    public void onMiningEventPacket(MiningEventPacket packet) {
        if (BBsentials.config.toDisplayConfig.getValue("disableAll")) {
            //its will returns false cause disabled is checked already before.
            Chat.sendPrivateMessageToSelf(packet.username + "There is a " + packet.event.getDisplayName() + "in the " + packet.island.getDisplayName() + " now/soon.");
        }
    }

    public void onWelcomePacket(WelcomeClientPacket packet) {
        if (packet.success) {
            BBsentials.config.bbsentialsRoles = packet.roles;
            Chat.sendPrivateMessageToSelf("§aLogin Success");
            if (!packet.motd.isEmpty()) {
                Chat.sendPrivateMessageToSelf(packet.motd);
            }
        }
        else {
            Chat.sendPrivateMessageToSelf("Login Failed");
        }
    }

    public void onDisconnectPacket(DisconnectPacket packet) {

    }

    public void onDisplayMessagePacket(DisplayMessagePacket packet) {
        Chat.sendPrivateMessageToSelf("§r"+packet.message);
    }

    public void onDisplayTellrawMessagePacket(DisplayTellrawMessagePacket packet) {
        /*Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(packet.rawJson));*/
        Chat.sendPrivateMessageToSelf("You received a tellraw Packet but it got ignored due too there being no safety checks in this version.");
    }

    public void onInternalCommandPacket(InternalCommandPacket packet) {

    }

    public void onInvalidCommandFeedbackPacket(InvalidCommandFeedbackPacket packet) {
        Chat.sendPrivateMessageToSelf(packet.displayMessage);
    }

    public void onPartyPacket(PartyPacket packet) {
        Chat.sendCommand("/p "+packet.type+String.join(" ",packet.users));
    }

    public void onSystemMessagePacket(SystemMessagePacket packet) {
        if (packet.important) {
            Chat.sendPrivateMessageToSelf("§c§n"+packet.message);
        }else {
            Chat.sendPrivateMessageToSelf("§r"+packet.message);
        }
        if (packet.ping){
            playsound(SoundEvents.ENTITY_WARDEN_DIG);
        }
    }


    public static void playsound(SoundEvent event){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance
                .master(event, 1.0F, 1.0F));
    }

}