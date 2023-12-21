package de.hype.bbsentials.client.common.mclibraries;


import de.hype.bbsentials.client.common.client.DebugThread;

public class EnvironmentCore {
    public static MCCommand commands;
    public static BBUtils utils;
    public static MCChat chat;
    public static MCUtils mcUtils;
    public static Options mcoptions;
    public static DebugThread debug;

    public EnvironmentCore(BBUtils utils, MCChat chat, MCUtils mcUtils, MCCommand commands, Options options, DebugThread debug) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.mcUtils = mcUtils;
        EnvironmentCore.commands = commands;
        EnvironmentCore.mcoptions = options;
        EnvironmentCore.debug=debug;
    }
}
