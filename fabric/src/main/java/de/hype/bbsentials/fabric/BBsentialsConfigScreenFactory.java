package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.ConfigManager;
import de.hype.bbsentials.shared.constants.Islands;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class BBsentialsConfigScreenFactory {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("BBsentials ConfigManager"));
        builder.setSavingRunnable(ConfigManager::saveAll);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory server = builder.getOrCreateCategory(Text.of("Server"));
        if (BBsentials.developerConfig.doDevDashboardConfig && BBsentials.generalConfig.hasBBRoles("dev")) {
            {
                ConfigCategory dev = builder.getOrCreateCategory(Text.of("§3Developing Dashboard"));
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Mode"), BBsentials.developerConfig.devMode)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Dev Mode"))
                        .setSaveConsumer(newValue -> BBsentials.developerConfig.devMode = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Detailed Dev Mode"), BBsentials.developerConfig.detailedDevMode)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Detailed Dev Mode"))
                        .setSaveConsumer(newValue -> BBsentials.developerConfig.detailedDevMode = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Bingo Time Override"), BBsentials.bbServerConfig.overrideBingoTime)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Always connect to the Server whether Bingo is going on or not."))
                        .setSaveConsumer(newValue -> BBsentials.bbServerConfig.overrideBingoTime = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Join Test server"), BBsentials.bbServerConfig.connectToBeta)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("§cWhen enabled join the testserver instead of the main. This Server is used for testing! Youre risking wrong invites etc."))
                        .setSaveConsumer(newValue -> BBsentials.bbServerConfig.connectToBeta = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Security"), BBsentials.developerConfig.devSecurity)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Shows dev debug even when its sensetive information"))
                        .setSaveConsumer(newValue -> BBsentials.developerConfig.devSecurity = newValue)
                        .build());

            }
        }
        {
            if (BBsentials.generalConfig.getUsername().equalsIgnoreCase("Hype_the_Time")) {
                server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), BBsentials.bbServerConfig.bbServerURL.replaceAll(".", "*"))
                        .setDefaultValue("static.88-198-149-240.clients.your-server.de")
                        .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                        .setSaveConsumer((newValue) -> {
                            if (newValue.replace("*", "").trim().isEmpty()) {
                                return;
                            }
                            else {
                                BBsentials.bbServerConfig.bbServerURL = newValue;
                            }
                        })
                        .build());
            }
            else {
                server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), BBsentials.bbServerConfig.bbServerURL)
                        .setDefaultValue("static.88-198-149-240.clients.your-server.de")
                        .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                        .setSaveConsumer(newValue -> BBsentials.bbServerConfig.bbServerURL = newValue)
                        .build());
            }
            server.addEntry(entryBuilder.startStrField(Text.of("BBsentials API key"), BBsentials.bbServerConfig.apiKey.replaceAll(".", "*"))
                    .setDefaultValue("unset")
                    .setTooltip(Text.of("Put you API Key here (the one generated in the Discord! with /link). §cThe Text is visually censored. Not saved unless you changed it."))
                    .setSaveConsumer((newValue) -> {
                        if (newValue.replace("*", "").trim().isEmpty()) {
                            return;
                        }
                        else {
                            BBsentials.bbServerConfig.apiKey = newValue;
                        }
                    })
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Connect to Test Server"), BBsentials.bbServerConfig.connectToBeta)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Makes you connect to the Test Server instead of the Main Server. Keep in mind that all announces may be tests and the main announces are not transferred over to here!")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> BBsentials.bbServerConfig.connectToBeta = newValue)
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Mojang Auth"), BBsentials.bbServerConfig.useMojangAuth)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Uses mojang as authenticator instead of api key"))
                    .setSaveConsumer(newValue -> BBsentials.bbServerConfig.useMojangAuth = newValue)
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Override Bingo Time"), BBsentials.bbServerConfig.overrideBingoTime)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Override the Bingo Time and connect always to the Server. (Bingo time is 14 days cause Extreme Bingo)"))
                    .setSaveConsumer(newValue -> BBsentials.bbServerConfig.overrideBingoTime = newValue)
                    .build());
        } // server
        ConfigCategory party = builder.getOrCreateCategory(Text.of("§6Party"));
        {
            party.addEntry(entryBuilder.startBooleanToggle(Text.of("Allow Server Partying"), BBsentials.partyConfig.allowServerPartyInvite)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Allow the Server to party players for you automatically. (Convenience Feature. Is used for example for services to automatically party the persons which joined it)"))
                    .setSaveConsumer(newValue -> BBsentials.partyConfig.allowServerPartyInvite = newValue)
                    .build());
        }//Party
        ConfigCategory visual = builder.getOrCreateCategory(Text.of("Visual"));
        {
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Gamma Override"), BBsentials.visualConfig.doGammaOverride)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to enable full bright"))
                    .setSaveConsumer(newValue -> BBsentials.visualConfig.doGammaOverride = newValue)
                    .requireRestart()
                    .build());
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Bingo Chat"), BBsentials.visualConfig.showBingoChat)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want the Bingo Chat to be show"))
                    .setSaveConsumer(newValue -> BBsentials.visualConfig.showBingoChat = newValue)
                    .build());
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Splash Status Updates"), BBsentials.splashConfig.showSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want to see Splash Staus updates. Keep in mind that this will only send you status updates for the Splashes which you were shown.\nThose hidden due too too high Splash Time will still remain invisible"))
                    .setSaveConsumer(newValue -> BBsentials.splashConfig.showSplashStatusUpdates = newValue)
                    .build());
        }
        //Visual
        ConfigCategory notifications = builder.getOrCreateCategory(Text.of("Notifications"));
        {
            BooleanListEntry doNotifications = entryBuilder.startBooleanToggle(Text.of("Do Desktop Notifications"), BBsentials.generalConfig.doDesktopNotifications)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                    .setSaveConsumer(newValue -> BBsentials.generalConfig.doDesktopNotifications = newValue)
                    .build();
            DropdownBoxEntry<String> notificationOn = entryBuilder.startStringDropdownMenu(Text.of("Notification on"), BBsentials.generalConfig.notifForMessagesType) // Start the StringDropdownMenu entry
                    .setSelections(List.of("all", "nick", "none"))
                    .setTooltip(Text.of("When do you want to receive Desktop Notifications? on all party, messages containing nickname"))
                    .setDefaultValue("all")
                    .setRequirement(Requirement.isTrue(doNotifications))
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> BBsentials.generalConfig.notifForMessagesType = newValue)
                    .build();
            StringListEntry nickname = entryBuilder.startStrField(Text.of("Nickname"), BBsentials.generalConfig.nickname)
                    .setDefaultValue("")
                    .setTooltip(Text.of("Nickname. you will get send desktop notifications if a message contains one"))
                    .setRequirement(() -> {
                        return doNotifications.getValue() && notificationOn.getValue().equals("nick");
                    })
                    .setSaveConsumer(newValue -> BBsentials.generalConfig.nickname = newValue)
                    .build();

            notifications.addEntry(doNotifications);
            notifications.addEntry(notificationOn);
            notifications.addEntry(nickname);
        }
        //Notifications
        ConfigCategory other = builder.getOrCreateCategory(Text.of("Other"));
        {
            other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept Reparties"), BBsentials.partyConfig.acceptReparty)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                    .setSaveConsumer(newValue -> BBsentials.partyConfig.acceptReparty = newValue)
                    .build());
            other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept auto invite"), BBsentials.partyConfig.allowBBinviteMe)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Do you want that whenever someone sends you a msg ending with 'bb:party me' to send them a party invite automatically?"))
                    .setSaveConsumer(newValue -> BBsentials.partyConfig.allowBBinviteMe = newValue)
                    .build());
            SubCategoryBuilder trolls = entryBuilder.startSubCategory(Text.of("Trolls")).setExpanded(false);
            BooleanListEntry swapActionBarAndChat = (entryBuilder.startBooleanToggle(Text.of("Actionbar-Chat switch"), BBsentials.funConfig.swapActionBarChat)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Swap that chat messages are shown in actionbar and reverse"))
                    .setSaveConsumer(newValue -> BBsentials.funConfig.swapActionBarChat = newValue)
                    .build());
            Requirement trollSwapEnabled = swapActionBarAndChat::getValue;
            BooleanListEntry swapActionBarAndChatOnlyNormal = (entryBuilder.startBooleanToggle(Text.of("Only normal messages"), BBsentials.funConfig.swapOnlyNormal)
                    .setDefaultValue(false)
                    .setRequirement(trollSwapEnabled)
                    .setTooltip(Text.of("Swap only the default messages (→ everything not from BBsentials)"))
                    .setSaveConsumer(newValue -> BBsentials.funConfig.swapOnlyNormal = newValue)
                    .build());
            BooleanListEntry swapActionBarAndChatOnlyBB = (entryBuilder.startBooleanToggle(Text.of("Only BBsentials messages"), BBsentials.funConfig.swapOnlyBBsentials)
                    .setDefaultValue(false)
                    .setRequirement(trollSwapEnabled)
                    .setTooltip(Text.of("Swap only the messages from BBsentials"))
                    .setSaveConsumer(newValue -> BBsentials.funConfig.swapOnlyBBsentials = newValue)
                    .build());
            trolls.add(swapActionBarAndChat);
            trolls.add(swapActionBarAndChatOnlyNormal);
            trolls.add(swapActionBarAndChatOnlyBB);
            other.addEntry(trolls.build());
        }
        //other
        ConfigCategory chChestItems = builder.getOrCreateCategory(Text.of("Ch Chest Items"));
        {
            BooleanListEntry allItems = entryBuilder.startBooleanToggle(Text.of("All Chest Items"), BBsentials.chChestConfig.allChChestItem)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select to receive notifications when an any Item is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.allChChestItem = newValue)
                    .build();
            chChestItems.addEntry(allItems);
            Requirement notAllItemsRequirement = () -> !allItems.getValue();
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("ALL Robo Parts "), BBsentials.chChestConfig.allRoboPart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when an allRoboPartCustomChChestItem is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.allRoboPart = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());
            BooleanListEntry allRoboParts = (entryBuilder.startBooleanToggle(Text.of("Custom (Other) Items"), BBsentials.chChestConfig.customChChestItem)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when any not already defined Item is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.customChChestItem = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());
            chChestItems.addEntry(allRoboParts);
            Requirement notAllRoboPartsRequirement = () -> !allRoboParts.getValue();
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Prehistoric Egg"), BBsentials.chChestConfig.prehistoricEgg)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Prehistoric Egg is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.prehistoricEgg = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Pickonimbus 2000"), BBsentials.chChestConfig.pickonimbus2000)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Pickonimbus 2000 is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.pickonimbus2000 = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());
            SubCategoryBuilder roboParts = entryBuilder.startSubCategory(Text.of("Robo Parts")).setRequirement(Requirement.all(notAllRoboPartsRequirement, notAllItemsRequirement)).setExpanded(true);
            roboParts.add(entryBuilder.startBooleanToggle(Text.of("Control Switch"), BBsentials.chChestConfig.controlSwitch)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Control Switch is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.controlSwitch = newValue)
                    .build());

            roboParts.add(entryBuilder.startBooleanToggle(Text.of("Electron Transmitter"), BBsentials.chChestConfig.electronTransmitter)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when an Electron Transmitter is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.electronTransmitter = newValue)
                    .build());

            roboParts.add(entryBuilder.startBooleanToggle(Text.of("FTX 3070"), BBsentials.chChestConfig.ftx3070)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a FTX 3070 is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.ftx3070 = newValue)
                    .build());

            roboParts.add(entryBuilder.startBooleanToggle(Text.of("Robotron Reflector"), BBsentials.chChestConfig.robotronReflector)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Robotron Reflector is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.robotronReflector = newValue)
                    .build());

            roboParts.add(entryBuilder.startBooleanToggle(Text.of("Superlite Motor"), BBsentials.chChestConfig.superliteMotor)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Superlite Motor is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.superliteMotor = newValue)
                    .build());

            roboParts.add(entryBuilder.startBooleanToggle(Text.of("Synthetic Heart"), BBsentials.chChestConfig.syntheticHeart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Synthetic Heart is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.syntheticHeart = newValue)
                    .build());
            chChestItems.addEntry(roboParts.build());
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Flawless Gemstone"), BBsentials.chChestConfig.flawlessGemstone)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when any Flawless Gemstone is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.flawlessGemstone = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Jungle Heart"), BBsentials.chChestConfig.jungleHeart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a JungleHeart is found"))
                    .setSaveConsumer(newValue -> BBsentials.chChestConfig.jungleHeart = newValue)
                    .setRequirement(notAllItemsRequirement)
                    .build());
        }//CHChestItems
        ConfigCategory miningEvents = builder.getOrCreateCategory(Text.of("Mining Events"));
        {
            BooleanListEntry allEvents = entryBuilder.startBooleanToggle(Text.of("All Events"), BBsentials.miningEventConfig.allEvents)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Get updated for any Mining Event"))
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.allEvents = newValue)
                    .build();
            miningEvents.addEntry(allEvents);
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("§cBlock Crystal Hollow Events"), BBsentials.miningEventConfig.blockChEvents)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Block getting Crystal Hollow Events. Maybe if you haven't accessed Crystal Hollows yet "))
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.blockChEvents = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Gone with the Wind"), BBsentials.miningEventConfig.goneWithTheWind) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Gone with the Wind happens in the specified Island"))
                    .setDefaultValue("none")
                    .setSuggestionMode(false)
                    .setRequirement(() -> !allEvents.getValue())
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.goneWithTheWind = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Better Together"), BBsentials.miningEventConfig.betterTogether) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Better Together happens in the specified Island"))
                    .setDefaultValue("none")
                    .setSuggestionMode(false)
                    .setRequirement(() -> !allEvents.getValue())
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.betterTogether = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Double Powder"), BBsentials.miningEventConfig.doublePowder) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Double Powder happens in the specified Island"))
                    .setDefaultValue("none")
                    .setRequirement(() -> !allEvents.getValue())
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.doublePowder = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Mithril Gourmand"), BBsentials.miningEventConfig.mithrilGourmand)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Mithril Gourmand happens"))
                    .setRequirement(() -> !allEvents.getValue())
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.mithrilGourmand = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Raffle"), BBsentials.miningEventConfig.raffle)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Raffle happens"))
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.raffle = newValue)
                    .setRequirement(() -> !allEvents.getValue())
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Goblin Raid"), BBsentials.miningEventConfig.goblinRaid)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Goblin Raid happens"))
                    .setSaveConsumer(newValue -> BBsentials.miningEventConfig.goblinRaid = newValue)
                    .setRequirement(() -> !allEvents.getValue())
                    .build());
        } //Mining Events
        if (BBsentials.discordConfig.discordIntegration) {
            ConfigCategory discordIntegration = builder.getOrCreateCategory(Text.of("§bDiscord"));
            {
                discordIntegration.addEntry(entryBuilder.startStrField(Text.of("DC Bot Token"), BBsentials.discordConfig.botToken)
                        .setDefaultValue("")
                        .setTooltip(Text.of("Whether you want to allow executing any command from remote. Is a security risk in case someone hacks your dc account."))
                        .setSaveConsumer(newValue -> BBsentials.discordConfig.botToken = newValue)
                        .build());
                discordIntegration.addEntry(entryBuilder.startBooleanToggle(Text.of("Always silent"), BBsentials.discordConfig.alwaysSilent)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Will always use the @silent tag and never ping you with notification."))
                        .setSaveConsumer(newValue -> BBsentials.discordConfig.alwaysSilent = newValue)
                        .build());
                discordIntegration.addEntry(entryBuilder.startBooleanToggle(Text.of("Allow Custom Commands"), BBsentials.discordConfig.allowCustomCommands)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Whether you want to allow executing any command from remote. Is a security risk in case someone hacks your dc account."))
                        .setSaveConsumer(newValue -> BBsentials.discordConfig.allowCustomCommands = newValue)
                        .build());
                discordIntegration.addEntry(entryBuilder.startBooleanToggle(Text.of("Use sync bot"), BBsentials.discordConfig.useBridgeBot)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Whether you want messages to be sent over to your discord as well."))
                        .setSaveConsumer(newValue -> BBsentials.discordConfig.useBridgeBot = newValue)
                        .build());
            }
        }//Discord
        if (BBsentials.generalConfig.hasBBRoles("dev")) {
            ConfigCategory dev = builder.getOrCreateCategory(Text.of("§3Developing"));
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Mode"), BBsentials.developerConfig.devMode)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Dev Mode"))
                    .setSaveConsumer(newValue -> BBsentials.developerConfig.devMode = newValue)
                    .build());
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Detailed Dev Mode"), BBsentials.developerConfig.detailedDevMode)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Detailed Dev Mode"))
                    .setSaveConsumer(newValue -> BBsentials.developerConfig.detailedDevMode = newValue)
                    .build());
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Security"), BBsentials.developerConfig.devSecurity)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Shows dev debug even when its sensetive information"))
                    .setSaveConsumer(newValue -> BBsentials.developerConfig.devSecurity = newValue)
                    .build());
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Dashboard"), BBsentials.developerConfig.doDevDashboardConfig)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("When opening the Config have a combined view for developer most used configs"))
                    .setSaveConsumer(newValue -> BBsentials.developerConfig.doDevDashboardConfig = newValue)
                    .build());
        }
        if (BBsentials.generalConfig.hasBBRoles("splasher")) {
            ConfigCategory splasher = builder.getOrCreateCategory(Text.of("§dSplashes"));
            BooleanListEntry updateSplashStatus = entryBuilder.startBooleanToggle(Text.of("Auto Update Statuses"), BBsentials.splashConfig.autoSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Automatically updates the Status of the Splash by sending packets to the Server"))
                    .setSaveConsumer(newValue -> BBsentials.splashConfig.autoSplashStatusUpdates = newValue)
                    .build();
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Splash Status Updates"), BBsentials.splashConfig.showSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want to see Splash Staus updates. Keep in mind that this will only send you status updates for the Splashes which you were shown.\nThose hidden due too too high Splash Time will still remain invisible"))
                    .setSaveConsumer(newValue -> BBsentials.splashConfig.showSplashStatusUpdates = newValue)
                    .build());
            splasher.addEntry(updateSplashStatus);
            BooleanListEntry showLeecherHud = entryBuilder.startBooleanToggle(Text.of("Do not show Splash Leecher Overlay"), BBsentials.splashConfig.autoSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Optional disabler for the Splash Leecher Overlay. Is normally automatically enabled when you announce a splash.\nAutomatically gets disabled when its marked done."))
                    .setSaveConsumer(newValue -> BBsentials.splashConfig.autoSplashStatusUpdates = newValue)
                    .setRequirement(updateSplashStatus::getValue)
                    .build();
            splasher.addEntry(showLeecherHud);
            entryBuilder.startBooleanToggle(Text.of("Show Music Pants Users"), BBsentials.splashConfig.autoSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Displays a small red note in front of the username if they wear music pants. Displays everybody with it not just non Bingos"))
                    .setRequirement(showLeecherHud::getValue)
                    .setSaveConsumer(newValue -> BBsentials.splashConfig.autoSplashStatusUpdates = newValue)
                    .build();

        }
        return builder.build();
    }
}
