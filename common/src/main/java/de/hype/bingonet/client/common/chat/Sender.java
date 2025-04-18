package de.hype.bingonet.client.common.chat;

import de.hype.bingonet.shared.constants.Formatting;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;

import java.util.ArrayList;
import java.util.List;

import static de.hype.bingonet.client.common.chat.Chat.sendPrivateMessageToSelfInfo;
import static de.hype.bingonet.client.common.chat.Chat.sendPrivateMessageToSelfText;

public class Sender {
    private final List<String> sendQueue;
    private final List<Double> sendQueueTiming;
    private final List<Boolean> hidden;


    public Sender() {
        this.sendQueue = new ArrayList<>();
        this.sendQueueTiming = new ArrayList<>();
        this.hidden = new ArrayList<>();
        startSendingThread();
    }

    public void addSendTask(String task, double timing) {
        sendPrivateMessageToSelfText(Message.of(Formatting.GREEN + "Scheduled send-task (as " + sendQueueTiming.size() + " in line): " + task + " | Delay: " + timing));
        synchronized (sendQueue) {
            sendQueueTiming.add(timing);
            sendQueue.add(task);
            hidden.add(false);
            sendQueue.notify(); // Notify the waiting thread that a new String has been added
        }
    }

    public void addHiddenSendTask(String task, double timing) {
        synchronized (sendQueue) {
            sendQueueTiming.add(timing);
            sendQueue.add(task);
            hidden.add(true);

            sendQueue.notify(); // Notify the waiting thread that a new String has been added
        }
    }

    public void addImmediateSendTask(String task) {
        synchronized (sendQueue) {
            sendQueueTiming.add(0, 0.0);
            sendQueue.add(0, task);
            hidden.add(false);

            sendQueue.notify(); // Notify the waiting thread that a new String has been added
        }
    }

    public void addSendTask(String task) {
        addSendTask(task, 1);
    }

    public void startSendingThread() {
        Thread sendingThread = new Thread(new SendingRunnable());
        sendingThread.start();
        System.out.println("Sender thread started.");
    }

    private class SendingRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.println("Sender thread waiting for tasks...");
                String task = getNextTask();
                if (task != null) {
                    System.out.println("Sender thread processing task: " + task);
                    send(task, sendQueueTiming.remove(0), hidden.remove(0));
                }
                else {
                    synchronized (sendQueue) {
                        try {
                            sendQueue.wait(); // Wait for new Send
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        private String getNextTask() {
            synchronized (sendQueue) {
                if (!sendQueue.isEmpty()) {
                    return sendQueue.remove(0);
                }
                return null;
            }
        }

        private void send(String toSend, double timing, boolean hidden) {
            try {
                Thread.sleep((long) (timing * 1000)); // Simulate the send operation
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            EnvironmentCore.chat.sendChatMessage(toSend);
            if (!hidden) {
                sendPrivateMessageToSelfInfo("Sent Command to Server: " + toSend);
            }
        }
    }
}
