package de.hype.bbsentials.environment.packetconfig;

import de.hype.bbsentials.client.common.client.BBsentials;

import java.awt.*;

public class EnvironmentPacketConfig {
    public static final String enviroment = "Client";
    public static final String notEnviroment = "Server";
    public static final int apiVersion = 1;
    public static Color getDefaultWaypointColor() {
        return BBsentials.visualConfig.waypointDefaultColor;
    }

    public static boolean getWaypointDefaultWithTracer() {
        return BBsentials.visualConfig.waypointDefaultWithTracer;
    }

    public static String getSelfUsername() {
        return BBsentials.generalConfig.getUsername();
    }
}
