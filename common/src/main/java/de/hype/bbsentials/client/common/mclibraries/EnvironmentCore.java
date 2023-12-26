package de.hype.bbsentials.client.common.mclibraries;


import de.hype.bbsentials.client.common.client.DebugThread;

public class EnvironmentCore {
    public static MCCommand commands;
    public static Utils utils;
    public static MCChat chat;
    public static Options mcoptions;
    public static MCEvents mcevents;
    public static DebugThread debug;

    public EnvironmentCore(Utils utils, MCEvents events, MCChat chat, MCCommand commands, Options options, DebugThread debug) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.commands = commands;
        EnvironmentCore.mcevents = events;
        EnvironmentCore.mcoptions = options;
        EnvironmentCore.debug=debug;
    }
}
