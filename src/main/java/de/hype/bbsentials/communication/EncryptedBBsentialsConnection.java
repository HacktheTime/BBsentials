package de.hype.bbsentials.communication;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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
    private boolean highlightItem = false;

    public interface MessageReceivedCallback {
        void onMessageReceived(String message);
    }

    public void connect(String serverIP, int serverPort) {
        try {
            // Erstellen Sie eine SSL-Verbindung
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Erstellen Sie einen SSL-Socket-Factory und einen SSL-Socket
            SSLSocketFactory sslSocketFactory;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows-Betriebssystem
                sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            } else {
                // Linux und andere Betriebssysteme
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init((KeyStore) null); // Lade die Standard-Rot-Zertifikate
                TrustManager[] trustManagers = tmf.getTrustManagers();

                sslContext.init(null, trustManagers, new SecureRandom());
                sslSocketFactory = sslContext.getSocketFactory();
            }

            socket = sslSocketFactory.createSocket(serverIP, serverPort);

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

            // Example: Sending a message to the server
            // sendMessage("Hello from client!");
            // More logic here...

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void setMessageReceivedCallback(MessageReceivedCallback callback) {
        this.messageReceivedCallback = callback;
    }

    public void sendMessage(String message) {
        messageQueue.offer(message);
    }

    public void sendHiddenMessage(String message) {
        if (BBsentials.getConfig().isDetailedDevModeEnabled()){
            Chat.sendPrivateMessageToSelf("BBDev-s: "+message);
        }
        writer.println(message);
    }

    public void onMessageReceived(String message) {
        if (message.startsWith("H-")) {
            if (message.equals("H-BB-Login: ")) {
                sendHiddenMessage(MinecraftClient.getInstance().player.getUuid().toString());
                sendHiddenMessage(BBsentials.getConfig().getApiKey());
                sendHiddenMessage("?getperms");
            }
            else if (message.contains("H-PotDurations?")) {
                System.out.println("worked");
                int remainingTimeInMinutes = 0;
                StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.JUMP_BOOST);
                if (potTimeRequest != null) {
                    Chat.sendPrivateMessageToSelf(String.valueOf(potTimeRequest.getAmplifier()));
                    if (true) {
                        remainingTimeInMinutes = (int) (potTimeRequest.getDuration() / 20.0);
                    }
                }
                sendHiddenMessage("?potduration " + remainingTimeInMinutes);
            }
            else if (message.startsWith("H-?splash")) {
                String[] arguments = message.split(" ", 4);
                Chat.sendPrivateMessageToSelf(arguments[2] + "is splashing in Hub #" + arguments[1] + " soon.");
                splashHighlightItem("Hub #" + arguments[1], 30000);
            }
            else if (message.startsWith("H-Roles")) {
                BBsentials.getConfig().bbsentialsRoles = message.replace("H-Roles ", "");
                BBsentials.refreshCommands();
            }
            else if (message.startsWith("H-chchest")) {
                String[] arguments = message.replace("H-chchest","").trim().split(" ", 5); // Split with limit of 5
                String item = arguments[0];
                int x = Integer.parseInt(arguments[1]);
                int y = Integer.parseInt(arguments[2]);
                int z = Integer.parseInt(arguments[3]);
                String inviteCommand = arguments[4];
                Chat.createClientSideTellraw("");
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
        highlightItem = true;
        executorService.schedule(() -> {
            highlightItem = false;
            try {
                socket.setSoTimeout(0);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }, displayTimeInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getItemName() {
        return itemName;
    }

    public boolean highlightItem() {
        return highlightItem;
    }

    //TODO socket verschlüsseln
    //TODO dyndns eintrag?
    //TODO search
    //[11:17:21] [Render thread/INFO] (Minecraft) [STDOUT]: {AttributeModifiers:[],display:{Lore:['{"italic":false,"extra":[{"color":"gray","text":"Players: 5/80"}],"text":""}','{"italic":false,"extra":[{"color":"dark_gray","text":"Server: mega38A"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"yellow","text":"Click to connect!"}],"text":""}'],Name:'{"text":"§r§6Splash Hub"}'},overrideMeta:1b}
}
