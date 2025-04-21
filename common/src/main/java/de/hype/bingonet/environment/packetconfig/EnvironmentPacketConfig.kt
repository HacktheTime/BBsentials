package de.hype.bingonet.environment.packetconfig;

import de.hype.bingonet.client.common.client.BingoNet;

import java.awt.*;

public class EnvironmentPacketConfig {
    public static final String enviroment = "Client";
    public static final String notEnviroment = "Server";
    public static final int apiVersion = 1;
    public static Color getDefaultWaypointColor() {
        return BingoNet.visualConfig.waypointDefaultColor;
    }

    public static boolean getWaypointDefaultWithTracer() {
        return BingoNet.visualConfig.waypointDefaultWithTracer;
    }

    public static String getSelfUsername() {
        return BingoNet.generalConfig.getUsername();
    }
}
