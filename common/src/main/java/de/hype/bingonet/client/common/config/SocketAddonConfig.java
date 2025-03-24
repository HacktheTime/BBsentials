package de.hype.bingonet.client.common.config;

import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;


public class SocketAddonConfig extends BingoNetConfig {
    public boolean addonDebug = false;
    public boolean addonChatDebug = false;
    public boolean useSocketAddons = false;
    public boolean allowAutomatedSending = false;
    public boolean allowTellraw = false;
    public boolean allowClientCommands = false;
    public boolean allowChatPrompt = false;

    public SocketAddonConfig(){
        super(1);
        doInit();
        if (EnvironmentCore.utils.getUsername().equals("Hype_the_Time")){
            useSocketAddons=true;
//            allowAutomatedSending=true;
            allowTellraw=true;
            allowClientCommands=true;
            allowChatPrompt=true;
        }
    }
    @Override
    public void setDefault() {
    }
}
