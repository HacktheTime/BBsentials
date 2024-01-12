package de.hype.bbsentials.client.common.config;
@AToLoadBBsentialsConfig

public class ChChestConfig implements BBsentialsConfig {
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

    @Override
    public void setDefault() {

    }
}
