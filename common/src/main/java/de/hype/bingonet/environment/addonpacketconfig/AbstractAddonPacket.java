package de.hype.bingonet.environment.addonpacketconfig;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.communication.BBsentialConnection;
import de.hype.bingonet.shared.packets.network.InvalidCommandFeedbackPacket;

import java.lang.reflect.Field;

public class AbstractAddonPacket {
    public final int apiVersionMin;
    public final int apiVersionMax;

    protected AbstractAddonPacket(int apiVersionMin, int apiVersionMax) {
        this.apiVersionMax = apiVersionMax;
        this.apiVersionMin = apiVersionMin;

    }
}