package de.hype.bbsentials.communication.old.de;

import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBsentials;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
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
            socket = new Socket(serverIP, serverPort);

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

        } catch (IOException e) {
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
        if (BBsentials.getConfig().isDetailedDevModeEnabled()) {
            Chat.sendPrivateMessageToSelf("BBDev-s: " + message);
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
                sendHiddenMessage("?potduration " + getPotTime());
            }
            else if (message.startsWith("H-?splash")) {
                String[] arguments = message.split(" ", 6);
                String splashMessage = "§6"+arguments[4] + " is splashing in Hub #" + arguments[1] + " soon.";
                if ((getPotTime()>=Integer.parseInt(arguments[2]))&&getPotTime()<=Integer.parseInt(arguments[3]))
                    if (arguments.length >= 6) {
                        splashMessage = splashMessage + " : " + arguments[5];
                    }
                Chat.sendPrivateMessageToSelf(splashMessage);
                splashHighlightItem("Hub #" + arguments[1], 30000);
            }
            else if (message.startsWith("H-Roles")) {
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
                String tellrawText = (
                        "{\"text\":\"BB: @username found one or more @item in a chest (@x @y @z). Click here to join\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"@inviteCommand\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"On clicking you will get invited to a party. Command executed: @inviteCommand\"]}}"
                );
                tellrawText = tellrawText.replace("@username", username).replace("@item", item).replace("@x", x + "").replace("@y", y + "").replace("@z", z + "").replace("@inviteCommand", inviteCommand);
                Chat.sendPrivateMessageToSelfText(Chat.createClientSideTellraw(tellrawText));
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

    public static int getPotTime() {
        int remainingTimeInMinutes = 0;
        StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.JUMP_BOOST);
        if (potTimeRequest != null) {
            Chat.sendPrivateMessageToSelf(String.valueOf(potTimeRequest.getAmplifier()));
            if (true) {
                remainingTimeInMinutes = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingTimeInMinutes;
    }
    //TODO socket verschlüsseln
    //TODO dyndns eintrag?
    //TODO search
    //[11:17:21] [Render thread/INFO] (Minecraft) [STDOUT]: {AttributeModifiers:[],display:{Lore:['{"italic":false,"extra":[{"color":"gray","text":"Players: 5/80"}],"text":""}','{"italic":false,"extra":[{"color":"dark_gray","text":"Server: mega38A"}],"text":""}','{"italic":false,"text":""}','{"italic":false,"extra":[{"color":"yellow","text":"Click to connect!"}],"text":""}'],Name:'{"text":"§r§6Splash Hub"}'},overrideMeta:1b}
}
