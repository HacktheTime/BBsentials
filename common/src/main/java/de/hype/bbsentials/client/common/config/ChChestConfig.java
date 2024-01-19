package de.hype.bbsentials.client.common.config;


public class ChChestConfig extends BBsentialsConfig {
    public boolean allChChestItem = true;
    public boolean allRoboPart = false;
    public boolean customChChestItem = false;

    public boolean prehistoricEgg = false;
    public boolean pickonimbus2000 = false;
    public boolean controlSwitch = false;
    public boolean electronTransmitter = false;
    public boolean ftx3070 = false;
    public boolean robotronReflector = false;
    public boolean superliteMotor = false;
    public boolean syntheticHeart = false;
    public boolean flawlessGemstone = false;
    public boolean jungleHeart = false;

    public boolean addWaypointForChests = true;

    public ChChestConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
