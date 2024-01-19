package de.hype.bbsentials.environment.addonpacketconfig;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.shared.packets.network.InvalidCommandFeedbackPacket;

import java.lang.reflect.Field;

public class AbstractAddonPacket {
    public final int apiVersionMin;
    public final int apiVersionMax;

    protected AbstractAddonPacket(int apiVersionMin, int apiVersionMax) {
        this.apiVersionMax = apiVersionMax;
        this.apiVersionMin = apiVersionMin;

    }
}