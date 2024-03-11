package de.hype.bbsentials.client.common.mclibraries;


import de.hype.bbsentials.client.common.client.DebugThread;

public class EnvironmentCore {
    public static MCCommand commands;
    public static Utils utils;
    public static MCChat chat;
    public static Options mcoptions;
    public static MCEvents mcevents;
    public static DebugThread debug;
    public static String versionType;

    private EnvironmentCore(Utils utils, MCEvents events, MCChat chat, MCCommand commands, Options options, DebugThread debug, String versionType) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.commands = commands;
        EnvironmentCore.mcevents = events;
        EnvironmentCore.mcoptions = options;
        EnvironmentCore.debug = debug;
        EnvironmentCore.versionType = versionType;
    }


    public static EnvironmentCore fabric(Utils utils, MCEvents events, MCChat chat, MCCommand commands, Options options, DebugThread debug) {
        return new EnvironmentCore(utils, events, chat, commands, options, debug, "modern");
    }

    public static EnvironmentCore forge(Utils utils, MCEvents events, MCChat chat, MCCommand commands, Options options, DebugThread debug) {
        return new EnvironmentCore(utils, events, chat, commands, options, debug, "1.8.9");
    }

    public static Boolean isFabric() {
        if (EnvironmentCore.versionType == null) return null;
        return versionType.equals("modern");
    }

    public static Boolean isForge1_8_9() {
        if (EnvironmentCore.versionType == null) return null;
        return versionType.equals("1.8.9");
    }
}
