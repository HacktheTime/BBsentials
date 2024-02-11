package de.hype.bbsentials.client.common.config;


import java.awt.*;

public class VisualConfig extends BBsentialsConfig {
    public boolean showBingoChat = true;
    public boolean doDesktopNotifications = false;
    public boolean doGammaOverride = true;
    public boolean waypointDefaultWithTracer = true;
    public Color waypointDefaultColor = Color.WHITE;
    public boolean showGoalCompletions = false;
    public boolean showCardCompletions = true;


    public VisualConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
