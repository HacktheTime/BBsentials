package de.hype.bingonet.client.common.config;


import java.awt.*;

public class VisualConfig extends BingoNetConfig {
    public boolean showBingoChat = true;
    public boolean doGammaOverride = true;
    public boolean waypointDefaultWithTracer = true;
    public Color waypointDefaultColor = Color.WHITE;
    public boolean showGoalCompletions = false;
    public boolean showCardCompletions = true;
    public boolean broadcastGoalAndCardCompletion = true;
    public boolean showContributorPositionInCount = true;
    public boolean addSplashWaypoint = true;
    public boolean infiniteChatHistory = true;
    public boolean showMODSolver = false;
    public String appendMinecraftWindowTitle = "%default%";


    public VisualConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
