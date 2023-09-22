package de.hype.bbsentials.client;

import de.hype.bbsentials.constants.enviromentShared.Islands;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

import static de.hype.bbsentials.client.BBsentials.config;

public class BBsentialsConfigScreemFactory {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("BBsentials Config"));
        //builder.setSavingRunnable(BBsentials.getConfig()::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory server = builder.getOrCreateCategory(Text.of("Server"));
        if (config.getUsername().equalsIgnoreCase("Hype_the_Time")) {
            server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), config.getBBServerURL().replaceAll(".", "*"))
                    .setDefaultValue("localhost")
                    .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                    .setSaveConsumer((newValue) -> {
                        if (newValue.replace("*", "").trim().isEmpty()) {
                            return;
                        }
                        else {
                            config.bbServerURL = newValue;
                        }
                    })
                    .build());
        }
        else {
            server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), config.getBBServerURL())
                    .setDefaultValue("localhost")
                    .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                    .setSaveConsumer(newValue -> config.bbServerURL = newValue)
                    .build());
        }

        server.addEntry(entryBuilder.startStrField(Text.of("BBsentials API key"), config.apiKey.replaceAll(".", "*"))
                .setDefaultValue("unset")
                .setTooltip(Text.of("Put you API Key here (the one generated in the Discord! with /link). §cThe Text is visually censored. Not saved unless you changed it."))
                .setSaveConsumer((newValue) -> {
                    if (newValue.replace("*", "").trim().isEmpty()) {
                        return;
                    }
                    else {
                        config.apiKey = newValue;
                    }
                })
                .build());
        server.addEntry(entryBuilder.startBooleanToggle(Text.of("Connect to Test Server"), config.connectToBeta)
                .setDefaultValue(false)
                .setTooltip(Text.of("Makes you connect to the Test Server instead of the Main Server. Keep in mind that all announces may be tests and the main announces are not transferred over to here!")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> config.connectToBeta = newValue)
                .build());
        server.addEntry(entryBuilder.startBooleanToggle(Text.of("Override Bingo Time"), config.overrideBingoTime)
                .setDefaultValue(false)
                .setTooltip(Text.of("Override the Bingo Time and connect always to the Server. (Bingo time is 14 days cause Extreme Bingo)"))
                .setSaveConsumer(newValue -> config.overrideBingoTime = newValue)
                .build());
        //Visual
        ConfigCategory visual = builder.getOrCreateCategory(Text.of("Visual"));
        visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Bingo Chat"), config.showBingoChat)
                .setDefaultValue(true)
                .setTooltip(Text.of("Select if you want the Bingo Chat to be show"))
                .setSaveConsumer(newValue -> config.showBingoChat = newValue)
                .build());
        visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Splash Status Updates"), config.showSplashStatusUpdates)
                .setDefaultValue(true)
                .setTooltip(Text.of("Select if you want to see Splash Staus updates. Keep in mind that this will only send you status updates for the Splashes which you were shown.\nThose hidden due too too high Splash Time will still remain invisible"))
                .setSaveConsumer(newValue -> config.showSplashStatusUpdates = newValue)
                .build());
        //Notifications
        ConfigCategory notifications = builder.getOrCreateCategory(Text.of("Notifications"));
        notifications.addEntry(entryBuilder.startBooleanToggle(Text.of("Do Desktop Notifications"), config.doDesktopNotifications)
                .setDefaultValue(true)
                .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                .setSaveConsumer(newValue -> config.doDesktopNotifications = newValue)
                .build());
        notifications.addEntry(entryBuilder.startStrField(Text.of("Nickname"), config.nickname)
                .setDefaultValue("")
                .setTooltip(Text.of("Nickname. you will get send desktop notifications if a message contains one"))
                .setSaveConsumer(newValue -> config.nickname = newValue)
                .build());
        notifications.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Notification on"), config.NotifForPartyMessagesType) // Start the StringDropdownMenu entry
                .setSelections(List.of("all", "nick", "none"))
                .setTooltip(Text.of("When do you want to receive Desktop Notifications? on all party, party messages containing nickname or no party messages"))
                .setDefaultValue("all")
                .setSuggestionMode(false)
                .setSaveConsumer(newValue -> config.NotifForPartyMessagesType = newValue)
                .build());
        //other
        ConfigCategory other = builder.getOrCreateCategory(Text.of("Other"));
        other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept Reparties"), config.acceptReparty)
                .setDefaultValue(true)
                .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                .setSaveConsumer(newValue -> config.showBingoChat = newValue)
                .build());
        other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept auto invite"), config.allowBBinviteMe)
                .setDefaultValue(true)
                .setTooltip(Text.of("Do you want that whenever someone sends you a msg ending with 'bb:party me' to send them a party invite automatically?"))
                .setSaveConsumer(newValue -> config.allowBBinviteMe = newValue)
                .build());
        ConfigCategory chChestItems = builder.getOrCreateCategory(Text.of("Ch Chest Items"));
        {
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("All Chest Items"), config.toDisplayConfig.allChChestItem)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select to receive notifications when an any Item is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.allChChestItem = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("ALL Robo Parts "), config.toDisplayConfig.allRoboPart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when an allRoboPartCustomChChestItem is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.allRoboPart = newValue)
                    .build());
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Custom (Other) Items"), config.toDisplayConfig.customChChestItem)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when any not already defined Item is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.customChChestItem = newValue)
                    .build());
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Prehistoric Egg"), config.toDisplayConfig.prehistoricEgg)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Prehistoric Egg is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.prehistoricEgg = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Pickonimbus 2000"), config.toDisplayConfig.pickonimbus2000)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Pickonimbus 2000 is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.pickonimbus2000 = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Control Switch"), config.toDisplayConfig.controlSwitch)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Control Switch is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.controlSwitch = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Electron Transmitter"), config.toDisplayConfig.electronTransmitter)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when an Electron Transmitter is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.electronTransmitter = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("FTX 3070"), config.toDisplayConfig.ftx3070)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a FTX 3070 is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.ftx3070 = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Robotron Reflector"), config.toDisplayConfig.robotronReflector)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Robotron Reflector is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.robotronReflector = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Superlite Motor"), config.toDisplayConfig.superliteMotor)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Superlite Motor is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.superliteMotor = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Synthetic Heart"), config.toDisplayConfig.syntheticHeart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a Synthetic Heart is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.syntheticHeart = newValue)
                    .build());

            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Flawless Gemstone"), config.toDisplayConfig.flawlessGemstone)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when any Flawless Gemstone is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.flawlessGemstone = newValue)
                    .build());
            chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Jungle Heart"), config.toDisplayConfig.jungleHeart)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Select to receive notifications when a JungleHeart is found"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.jungleHeart = newValue)
                    .build());
        }//CHChestItems
        ConfigCategory miningEvents = builder.getOrCreateCategory(Text.of("Mining Events"));
        {
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("All Events"), config.toDisplayConfig.allEvents)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Get updated for any Mining Event"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.allEvents = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("§cBlock Crystal Hollow Events"), config.toDisplayConfig.blockChEvents)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Block getting Crystal Hollow Events. Maybe if you haven't accessed Crystal Hollows yet "))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.blockChEvents = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Gone with the Wind"), config.toDisplayConfig.goneWithTheWind) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Gone with the Wind happens in the specified Island"))
                    .setDefaultValue("none")
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> config.toDisplayConfig.goneWithTheWind = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Better Together"), config.toDisplayConfig.betterTogether) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Better Together happens in the specified Island"))
                    .setDefaultValue("none")
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> config.toDisplayConfig.betterTogether = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Double Powder"), config.toDisplayConfig.doublePowder) // Start the StringDropdownMenu entry
                    .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                    .setTooltip(Text.of("Get notified when a Double Powder happens in the specified Island"))
                    .setDefaultValue("none")
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> config.toDisplayConfig.doublePowder = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Mithril Gourmand"), config.toDisplayConfig.mithrilGourmand)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Mithril Gourmand happens"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.mithrilGourmand = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Raffle"), config.toDisplayConfig.raffle)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Raffle happens"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.raffle = newValue)
                    .build());
            miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Goblin Raid"), config.toDisplayConfig.goblinRaid)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Get notified when a Goblin Raid happens"))
                    .setSaveConsumer(newValue -> config.toDisplayConfig.goblinRaid = newValue)
                    .build());
        } //Mining Events
        if (config.hasBBRoles("dev")){
            ConfigCategory dev = builder.getOrCreateCategory(Text.of("§3Developing"));
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Mode"), config.devMode)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Dev Mode"))
                    .setSaveConsumer(newValue -> config.devMode = newValue)
                    .build());
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Detailed Dev Mode"), config.detailedDevMode)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Detailed Dev Mode"))
                    .setSaveConsumer(newValue -> config.detailedDevMode = newValue)
                    .build());
        }
        if (config.hasBBRoles("splasher")){
            ConfigCategory dev = builder.getOrCreateCategory(Text.of("§dSplashes"));
            dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Auto Update Statuses"), config.autoSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Automatically updates the Status of the Splash by sending packets to the Server"))
                    .setSaveConsumer(newValue -> config.autoSplashStatusUpdates = newValue)
                    .build());
        }

        return builder.build();
    }
}
