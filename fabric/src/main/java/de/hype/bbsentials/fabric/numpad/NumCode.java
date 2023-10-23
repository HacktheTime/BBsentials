package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.common.api.Formatting;
import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.BBsentials;

public class NumCode {
    public String command;
    public String code;
    public boolean codeIsTransient;
    public Formatting formatting;
    public String requiredPermission;
    public transient Runnable toPerform;

    public NumCode(String code, String command) {
        this.command = command;
        this.code = code;
        this.codeIsTransient = false;
        this.formatting = Formatting.DARK_GREEN;
        requiredPermission = "";
    }

    public NumCode(String code, Formatting format, String requiredRole, Runnable toPerform) {
        this.command = "";
        this.code = code;
        this.formatting = format;
        requiredPermission = requiredRole;
        this.toPerform = toPerform;
        codeIsTransient = true;
    }

    public void execute() {
        if (!BBsentials.config.hasBBRoles(requiredPermission)) {
            Chat.sendPrivateMessageToSelfError("You don't have the required permissions to run '" + code + "' !");
            return;
        }
        if (!command.isEmpty()) {
            BBsentials.config.sender.addImmediateSendTask(command);
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
        Chat.sendPrivateMessageToSelfInfo("'" + code + "' executed.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            return ((NumCode) obj).code.equals(code);
        }
        return false;
    }

}
