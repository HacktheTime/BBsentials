package de.hype.bingonet.client.common.mclibraries;


import de.hype.bingonet.client.common.client.DebugThread;

public class EnvironmentCore {
    public static MCCommand commands;
    public static Utils utils;
    public static MCChat chat;
    public static MCEvents mcevents;
    public static DebugThread debug;
    public static String versionType;
    public static TextUtils textutils;
    public static WorldUtils worldUtils;

    private EnvironmentCore(Utils utils, MCEvents events, MCChat chat, MCCommand commands, DebugThread debug, TextUtils textUtils, String versionType, WorldUtils worldUtils) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.textutils = textUtils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.commands = commands;
        EnvironmentCore.mcevents = events;
        EnvironmentCore.debug = debug;
        EnvironmentCore.versionType = versionType;
        EnvironmentCore.worldUtils = worldUtils;
    }


    public static EnvironmentCore fabric(Utils utils, MCEvents events, MCChat chat, MCCommand commands, DebugThread debug, TextUtils textUtils, WorldUtils worldUtils) {
        return new EnvironmentCore(utils, events, chat, commands, debug, textUtils, "modern", worldUtils);
    }

    public static EnvironmentCore forge(Utils utils, MCEvents events, MCChat chat, MCCommand commands, DebugThread debug, TextUtils textUtils, WorldUtils worldUtils) {
        return new EnvironmentCore(utils, events, chat, commands, debug, textUtils, "1.8.9", worldUtils);
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
