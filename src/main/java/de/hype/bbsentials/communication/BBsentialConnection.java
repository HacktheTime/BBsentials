package de.hype.bbsentials.communication;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import javax.net.ssl.*;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BBsentialConnection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private LinkedBlockingQueue<String> messageQueue;
    private MessageReceivedCallback messageReceivedCallback;
    private ScheduledExecutorService executorService;
    private String itemName = "Hub #0";

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
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
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
                    }
            };

            // Create an SSL context with the custom TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());

            // Create an SSL socket factory
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);

            socket.setKeepAlive(true); // Enable Keep-Alive

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            messageQueue = new LinkedBlockingQueue<>();

            executorService = new ScheduledThreadPoolExecutor(2); // Adjust the pool size as needed

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
        if (message.startsWith("H-")) {
            if (message.equals("H-BB-Login: ")) {
                Chat.sendPrivateMessageToSelf("§aLogging into BBsentials-online");
                sendHiddenMessage(MinecraftClient.getInstance().player.getUuid().toString().replace("-", ""));
                writer.println(BBsentials.getConfig().getApiKey());
                sendHiddenMessage("?getperms");
            }
            else if (message.contains("H-potdurations?")) {
                sendHiddenMessage("?potduration " + getPotTime());
            }
            else if (message.startsWith("H-splash")) {
                String[] arguments = message.split(" ", 7);
                int min = (1 * 60 * 60) - Integer.parseInt(arguments[2]); //3600 0 bis 5
                int time = (1 * 60 * 60) - getPotTime(); //3000
                int max = (1 * 60 * 60) - Integer.parseInt(arguments[3]); //3300
                if ((time <= min) && (time >= max)) {
                    if (arguments.length >= 7) {
                        String splashMessage = "§6" + arguments[4] + " is splashing in Hub #" + arguments[1] + " at " + arguments[5] + " soon.";
                        splashMessage = splashMessage + " : " + arguments[6];
                        Chat.sendPrivateMessageToSelf(splashMessage);
                        splashHighlightItem("Hub #" + arguments[1], 30000);
                    }
                }
            }
            else if (message.startsWith("H-roles")) {
                BBsentials.getConfig().bbsentialsRoles = message.replace("H-Roles ", "");
                BBsentials.refreshCommands();
            }
            else if (message.startsWith("H-chchest")) {
                String[] arguments = message.replace("H-chchest", "").trim().split(" ", 6); // Split with limit of 5
                String username = arguments[0];
                String item = arguments[1];
                int x = Integer.parseInt(arguments[2]);
                int y = Integer.parseInt(arguments[3]);
                int z = Integer.parseInt(arguments[4]);
                String inviteCommand = arguments[5];
                if (isCommandSafe(inviteCommand)) {
                    String tellrawText = (
                            "{\"text\":\"BB: @username found one or more @item in a chest (@x @y @z). Click here to join\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}"
                    );
                    tellrawText = tellrawText.replace("@username", username).replace("@item", item).replace("@x", x + "").replace("@y", y + "").replace("@z", z + "").replace("@inviteCommand", inviteCommand);
                    Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(tellrawText));
                }
            }
            else if (message.startsWith("H-event")) {
                String[] arguments = message.replace("H-event", "").trim().split(" ");
                Chat.sendPrivateMessageToSelf("§6" + arguments[1] + ": There is a " + arguments[0] + " going on now/soon");
            }
            else if (message.startsWith("H-reconnect")) {
                Chat.sendPrivateMessageToSelf("§4BBserver-online is going to restart soon. You will be automatically reconnected.\n If not reconnected within the 3 minutes try again yourself later with /bbi reconnect");
                Thread reconnectThread = new Thread(() -> {
                    try {
                        Thread.sleep((long) ((30 * 1000) + (Math.random() * 30 * 1000)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BBsentials.connectToBBserver();
                    if (!socket.isConnected()) {
                        try {
                            Thread.sleep((long) ((30 * 1000) + (Math.random() * 30 * 1000)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        BBsentials.connectToBBserver();
                    }
                });
                reconnectThread.start();
            }
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

    public void splashHighlightItem(String itemName, long displayTimeInMilliseconds) {
        this.itemName = itemName;
        BBsentials.config.highlightitem = true;
        executorService.schedule(() -> {
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

    public static boolean isCommandSafe(String command){
        if (command.startsWith("/p ") || command.startsWith("/party ") || command.startsWith("/boop ") || command.startsWith("/msg ")||command.startsWith("/hub ")) {
            return true;
        }else {
            BBsentials.bbserver.sendCommand("?emergency server-hacked? chchest command " + command);
            String emergencyMessage = "We detected that there was a command used which is not configured to be safe! "+command+" please check if its safe. IMMEDIATELY report this to the Admins and Developer Hype_the_Time (@hackthetime). If it is not safe immediately remove BBsentials!!!!!!!! ";
            System.out.println(emergencyMessage);
            Chat.sendPrivateMessageToSelf("§4"+emergencyMessage+"\n\n");
            Chat.sendPrivateMessageToSelf("§4"+emergencyMessage+"\n\n");
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(emergencyMessage);*/
        }
        return false;
    }
}
