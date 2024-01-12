package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.client.common.api.Formatting;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

import java.util.ArrayList;
import java.util.List;

public class NumCode {
    public List<String> commands;
    public List<Double> commandDelay;
    public String code;
    public boolean codeIsTransient;
    public Formatting formatting;
    public String requiredPermission;
    public transient Runnable toPerform;

    public NumCode(String code, String command) {
        this.commands = new ArrayList<>(List.of(command));
        this.commandDelay = new ArrayList<>(List.of(1.1));
        this.code = code;
        this.codeIsTransient = false;
        this.formatting = Formatting.DARK_GREEN;
        requiredPermission = "";
    }

    public NumCode(String code, List<String> command, List<Double> commandDelay) {
        this.commands = new ArrayList<>(command);
        this.commandDelay = new ArrayList<>(commandDelay);
        this.code = code;
        this.codeIsTransient = false;
        this.formatting = Formatting.DARK_GREEN;
        requiredPermission = "";
    }

    public NumCode(String code, Formatting format, String requiredRole, Runnable toPerform) {
        this.commands = new ArrayList<>();
        this.code = code;
        this.formatting = format;
        requiredPermission = requiredRole;
        this.toPerform = toPerform;
        codeIsTransient = true;
    }

    public int pressCount() {
        int count = commands.size() - (code.length());
        if (count < 0) count = 0;
        return count;
    }

    public void execute() {
        if (!BBsentials.generalConfig.hasBBRoles(requiredPermission)) {
            Chat.sendPrivateMessageToSelfError("You don't have the required permissions to run '" + code + "' ! (Required: '" + requiredPermission + "')");
            return;
        }
        if (!commands.isEmpty()) {
            for (int i = 0; i < commands.size(); i++) {
                String command = commands.get(i);
                if (command.startsWith("\\")) {
                    try {
                        Thread.sleep((long) (commandDelay.get(i)*1000));
                    } catch (InterruptedException ignored) {

                    }
                    if (ClientCommandInternals.executeCommand(command.replaceFirst("\\\\", ""))) {
                        Chat.sendPrivateMessageToSelfSuccess("Code '" + code + "': Success");
                    }
                    else {
                        Chat.sendPrivateMessageToSelfError("Code '" + code + "': Error Occurd. \nCommand: " + command);
                    }
                }
                else
                    BBsentials.sender.addHiddenSendTask(commands.get(i), commandDelay.get(i));
            }
        }
        else {
            if (toPerform == null) {
                Chat.sendPrivateMessageToSelfError("The specified code doesn't do anything");
                return;
            }
            try {
                BBsentials.executionService.execute(toPerform);
            } catch (Exception e) {
                Chat.sendPrivateMessageToSelfError(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        String toReturn = code;
        if (commands != null) {
            if (commands.isEmpty()) {
                toReturn += ": " + String.join(", ", commands);
            }
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            return ((NumCode) obj).code.equals(code);
        }
        return false;
    }

}
