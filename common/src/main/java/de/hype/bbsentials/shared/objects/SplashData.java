package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.StatusConstants;

public abstract class SplashData {
    public String announcer;
    public StatusConstants status;
    public int splashId;
    public int hubNumber;
    public String locationInHub;
    public Islands hubType;
    public String extraMessage;
    public boolean lessWaste;

    public SplashData(String user, int hubNumber, String locationInHub, Islands hubType, String extraMessage, boolean lessWaste) throws Exception {
        this.announcer = user;
        this.hubNumber = hubNumber;
        this.locationInHub = locationInHub;
        this.hubType = hubType;
        this.extraMessage = extraMessage.replace("&", "§");
        this.lessWaste = lessWaste;
        if (!(hubType.equals(Islands.HUB) || hubType.equals(Islands.DUNGEON_HUB))) {
            throw new Exception("§cInvalid hub type specified. Please only use the Suggestions!");
        }
        this.status = StatusConstants.WAITING;
    }

    public SplashData(String user, int splashId, int hubNumber, String locationInHub, Islands hubType, String extraMessage, boolean lessWaste, StatusConstants status) throws Exception {
        this.splashId = splashId;
        this.announcer = user;
        this.hubNumber = hubNumber;
        this.locationInHub = locationInHub;
        this.hubType = hubType;
        this.extraMessage = extraMessage.replace("&", "§");
        this.lessWaste = lessWaste;
        if (!(hubType.equals(Islands.HUB) || hubType.equals(Islands.DUNGEON_HUB))) {
            throw new Exception("§cInvalid hub type specified. Please only use the Suggestions!");
        }
        this.status = status;
    }

    public abstract void statusUpdate(StatusConstants newStatus);

    public void setWaiting() {
        statusUpdate(StatusConstants.WAITING);
    }

    public void setFull() {
        statusUpdate(StatusConstants.FULL);
    }

    public void setSplashing() {
        statusUpdate(StatusConstants.SPLASHING);
    }

    public void setDone() {
        statusUpdate(StatusConstants.DONEBAD);
    }

    public void setCanceled() {
        statusUpdate(StatusConstants.CANCELED);
    }

    public boolean stillAnnounce() {
        return status.equals(StatusConstants.WAITING);
    }
}