package de.hype.bbsentials.api;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class FromChatSocket implements Runnable {
    private Socket socket;
    private Thread thread;
    private PrintWriter out;

    // Konstruktor, um den Client-Thread zu starten
    public FromChatSocket() {
        // Starte den Client-Thread
        /*thread = new Thread(this);
        thread.start();
        System.out.println("Thread started");*/
    }

    // Implementierung der run() Methode des Runnable-Interfaces
    public void run() {
        try {
            boolean connected = false;
            // Endlosschleife, um auf Verbindung zum Server zu warten
            while (true) {
                try {
                    // Verbinden zum Server auf Port 8001
                    socket = new Socket("localhost", 8001);
                    connected = true;
                } catch (IOException e) {
                    System.out.println("Waiting for server on port 8001...");
                    Thread.sleep(1000); // Warte 1 Sekunde, bevor erneut versucht wird, eine Verbindung aufzubauen
                }
            }
            /*if (socket != null) {
                // Erstelle einen BufferedReader, um Daten vom Socket zu lesen
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boolean notNull = true;
                // Endlosschleife, um auf eingehende Nachrichten zu warten
                while (MinecraftClient.getInstance().isRunning() && socket.isConnected() && notNull) {

                }

                // Schließen des BufferedReader und des Sockets
                in.close();
                socket.close();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
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
/*
* try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        String plOutput = mc.getNetworkHandler().onChat.getChatGUI().getChatComponent(0).getUnformattedText();
        List<String> partyMembers = Arrays.asList(plOutput.split("\\r?\\n")).stream()
                .filter(s -> s.startsWith("Party Members:"))
                .map(s -> s.substring(16).replaceAll(" ●", ""))
                .collect(Collectors.toList());

        mc.player.networkHandler.sendChatMessage("/p disband");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        String currentPlayer = String.valueOf(mc.player.getName());
        partyMembers.stream()
                .filter(s -> !s.equals(currentPlayer))
                .forEach(s -> {
                    mc.player.networkHandler.sendChatMessage("/p invite " + s);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                });*/