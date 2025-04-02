package de.hype.bingonet.shared.objects;

import de.hype.bingonet.shared.constants.Islands;
import de.hype.bingonet.shared.constants.StatusConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SplashData {
    public String announcer;
    public StatusConstants status;
    public int splashId;
    public SplashLocation locationInHub;

    public String extraMessage;
    public boolean lessWaste;
    public String serverID;
    /**
     * If null the Splash is in a private Mega → Request Invite.
     */
    @Nullable
    public HubSelectorData hubSelectorData;

    public SplashData(String user, SplashLocation locationInHub, String extraMessage, boolean lessWaste, String serverID, @Nullable HubSelectorData hubSelectorData, StatusConstants status) {
        this.serverID = serverID;
        this.announcer = user;
        this.locationInHub = locationInHub;
        this.extraMessage = extraMessage != null ? extraMessage.replace("&", "§") : null;
        this.lessWaste = lessWaste;
        this.status = status;
        this.hubSelectorData = hubSelectorData;
    }

    public SplashData(String user, SplashLocation locationInHub, String extraMessage, boolean lessWaste, String serverID, @Nullable HubSelectorData hubSelectorData) {
        this(user, locationInHub, extraMessage, lessWaste, serverID, hubSelectorData, StatusConstants.WAITING);
    }

    public SplashData(SplashData packet) {
        this(packet.announcer, packet.locationInHub, packet.extraMessage, packet.lessWaste, packet.serverID, packet.hubSelectorData, packet.status);
        this.splashId = packet.splashId;
    }


    public static class HubSelectorData {
        public int hubNumber;
        public Islands hubType;

        public HubSelectorData(int hubNumber, @NotNull Islands hubType) {
            if (!(hubType.equals(Islands.HUB) || hubType.equals(Islands.DUNGEON_HUB))) {
                throw new IllegalArgumentException("§cInvalid hub type specified. Please only use the Suggestions!");
            }
            if (hubNumber < 1 || hubNumber > 28) {
                throw new IllegalArgumentException("§cInvalid hub number specified. Must be between 1 and 28");
            }
            this.hubNumber = hubNumber;
            this.hubType = hubType;
        }
    }
}