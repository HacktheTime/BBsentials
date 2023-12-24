package de.hype.bbsentials.client.common.mclibraries;


import de.hype.bbsentials.client.common.client.DebugThread;

public class EnvironmentCore {
    public static MCCommand commands;
    public static Utils utils;
    public static MCChat chat;
    public static Options mcoptions;
    public static DebugThread debug;

    public EnvironmentCore(Utils utils, MCChat chat, MCCommand commands, Options options, DebugThread debug) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.commands = commands;
        EnvironmentCore.mcoptions = options;
        EnvironmentCore.debug=debug;
    }
}
