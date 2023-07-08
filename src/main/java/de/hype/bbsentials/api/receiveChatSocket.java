package de.hype.bbsentials.api;

import de.hype.bbsentials.client.BBsentials;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class receiveChatSocket implements Runnable {
    private Thread thread;
    private ServerSocket serverSocket;

    // Konstruktor, um den Server-Thread zu starten
    public receiveChatSocket() {
        try {
            // Erstelle einen Socket auf Port 8000
            serverSocket = new ServerSocket(8000);
            System.out.println("Server started");
            // Starte den Server-Thread
            thread = new Thread(this);
            thread.start();
            System.out.println("Thread startet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendServerMessage(String message) {
        // Stellen Sie eine Verbindung zum Server her
        if (message == null) {
            System.out.println("Message is null: " + message);
        } else {
            BBsentials.getConfig().sender.addSendTask(message);
        try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Implementierung der run() Methode des Runnable-Interfaces
    public void run() {
        try {
            // Endlosschleife, um auf eingehende Verbindungen zu warten
            while (true) {
                // Warten auf eine eingehende Verbindung
                System.out.println("Waiting for incoming Chat Gen connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Chat Gen Client connected.");

                // Erstelle einen BufferedReader, um Daten vom Socket zu lesen
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                boolean notNull = true;
                // Endlosschleife, um auf eingehende Nachrichten zu warten
                while (MinecraftClient.getInstance().isRunning() && clientSocket.isConnected() && notNull) {
                    System.out.println("running");
                    // Lesen der Nachricht vom Socket
                    String message = in.readLine();
                    out.println("Recieved the following message: " + message);

                    if (message.contains("fov")) {
                        Options.setFov(130);
                    }
                    // Senden der Nachricht an den Minecraft-Chat
                    while (!isInGame()) {
                        try {
                            Thread.sleep(1000); // Wartezeit in Millisekunden
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sendServerMessage(message);
                    System.out.println("Message received and sent to Minecraft chat: " + message);
                    if (message == null) {
                        notNull = false;
                    } else {
                        Thread.sleep(300);
                    }
                }


                // Schließen des BufferedReader und des Sockets
                in.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isInGame() {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        if (minecraft.world != null || minecraft.getNetworkHandler() != null) {
            // Das Spiel befindet sich in einer Welt oder auf einem Server
            return true;
        } else {
            // Das Spiel befindet sich im Hauptmenü oder in einem anderen Menü
            return false;
        }
    }

}