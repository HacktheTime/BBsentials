package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

@AToLoadBBsentialsConfig
public class SocketAddonConfig  implements BBsentialsConfig{
    public boolean useSocketAddons = false;
    public boolean allowAutomatedSending = false;
    public boolean allowTellraw = false;
    public boolean allowClientCommands = false;
    public boolean allowChatPrompt = false;

    public SocketAddonConfig(){
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
