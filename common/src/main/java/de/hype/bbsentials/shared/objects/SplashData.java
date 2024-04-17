package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.StatusConstants;

public class SplashData {
    public String announcer;
    public StatusConstants status;
    public int splashId;
    public int hubNumber;
    public SplashLocation locationInHub;
    public Islands hubType;
    public String extraMessage;
    public boolean lessWaste;
    public String serverID;

    public SplashData(String user, int hubNumber, SplashLocation locationInHub, Islands hubType, String extraMessage, boolean lessWaste, String serverID) throws Exception {
        this.serverID = serverID;
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

    public SplashData(String user, int splashId, int hubNumber, SplashLocation locationInHub, Islands hubType, String extraMessage, boolean lessWaste, StatusConstants status, String serverID) throws Exception {
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
        this.serverID = serverID;
        this.status = status;
    }
}