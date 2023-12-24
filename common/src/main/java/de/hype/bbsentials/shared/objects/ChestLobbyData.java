package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.packets.mining.ChChestPacket;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ChestLobbyData {
    public String contactMan;
    public List<ChChestData> chests = new ArrayList<>();
    public String bbcommand;
    public String extraMessage;
    public transient Color color;
    public int lobbyId;
    public String serverId;
    protected String status;
    protected List<String> playersStillIn = new ArrayList<>();
    protected Date closingTime;

    public ChestLobbyData(Collection<ChChestData> chest, String serverId, String bbcommand, String extraMessage, Object status) {
        chests.addAll(chest);
        this.serverId = serverId;
        this.contactMan = chests.get(0).finder;
        this.bbcommand = bbcommand;
        this.extraMessage = extraMessage;
        setStatusNoOverride(status);
    }

    public ChChestPacket getAsPacket() {
        return new ChChestPacket(this);
    }

    public String getStatus() {
        return status;
    }

    /**
     * @param statusBase String or StatusConstants as type.
     * @throws IllegalArgumentException if Object is not a {@link String} or {@link StatusConstants}
     */
    public void setStatus(Object statusBase) throws SQLException {
        setStatusNoOverride(statusBase);
    }

    public void setStatusNoOverride(Object statusBase) {
        if (statusBase instanceof StatusConstants) {
            StatusConstants statusConstants = (StatusConstants) statusBase;
            this.status = statusConstants.getDisplayName();
            color = statusConstants.getColor();
        }
        else if (statusBase instanceof String) {
            this.status = (String) statusBase;
        }
        else {
            throw new IllegalArgumentException("Invalid input type. Expected String or StatusConstants.");
        }
    }

    public void addChest(ChChestData chest) {
        chests.add(chest);
    }

    public void transferToUser(String newContactMan) {
        contactMan = newContactMan;
    }

    public void setLobbyMetaData(List<String> playersStillIn, Date closingTime) throws SQLException {
        if (playersStillIn != null) {
            this.playersStillIn = playersStillIn;
        }
        if (closingTime != null) {
            this.closingTime = closingTime;
            onLobbyUpdate();
        }
    }

    public void onLobbyUpdate() {
        // need to be overridden
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChestLobbyData)) return false;
        return ((ChestLobbyData) obj).lobbyId == lobbyId;
    }
}
