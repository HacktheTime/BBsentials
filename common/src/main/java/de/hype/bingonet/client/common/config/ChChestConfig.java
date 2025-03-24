package de.hype.bingonet.client.common.config;


import java.awt.*;

public class ChChestConfig extends BingoNetConfig {
    public boolean allChChestItem = true;
    public boolean allRoboPart = false;
    public boolean customChChestItem = false;
    public boolean addWaypointForChests = true;
    public boolean doChestWaypointsTracers = true;
    public boolean doChestOverlay = true;
    public Color defaultWaypointColor = Color.GREEN;


    public boolean prehistoricEgg = false;
    public boolean pickonimbus2000 = false;
    public boolean controlSwitch = false;
    public boolean electronTransmitter = false;
    public boolean ftx3070 = false;
    public boolean robotronReflector = false;
    public boolean superliteMotor = false;
    public boolean syntheticHeart = false;
    public boolean flawlessGemstone = false;
    public boolean hideLootChestUnimportant = false;


    public ChChestConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
