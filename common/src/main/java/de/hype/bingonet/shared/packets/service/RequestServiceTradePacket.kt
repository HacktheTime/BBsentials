package de.hype.bingonet.shared.packets.service;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.constants.TradeType;

public class RequestServiceTradePacket extends AbstractPacket {
    public final TradeType tradeType;

    public RequestServiceTradePacket(TradeType tradeType) {
        super(1, 1);
        this.tradeType = tradeType;
    }
}
