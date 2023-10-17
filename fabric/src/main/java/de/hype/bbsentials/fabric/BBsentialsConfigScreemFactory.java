package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.constants.enviromentShared.Islands;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class BBsentialsConfigScreemFactory {
    public static Screen create(Screen parent) {
        if (BBsentials.config == null) {
            return new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(parent), Text.of("BBsentials"), Text.of("You need to login to a Server for the Config to be loaded."));
        }
        else {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("BBsentials Config"));
            builder.setSavingRunnable(BBsentials.getConfig()::save);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory server = builder.getOrCreateCategory(Text.of("Server"));
            if (BBsentials.config.getUsername().equalsIgnoreCase("Hype_the_Time")) {
                server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), BBsentials.config.getBBServerURL().replaceAll(".", "*"))
                        .setDefaultValue("localhost")
                        .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                        .setSaveConsumer((newValue) -> {
                            if (newValue.replace("*", "").trim().isEmpty()) {
                                return;
                            }
                            else {
                                BBsentials.config.bbServerURL = newValue;
                            }
                        })
                        .build());
            }
            else {
                server.addEntry(entryBuilder.startTextField(Text.of("Server URL"), BBsentials.config.getBBServerURL())
                        .setDefaultValue("localhost")
                        .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                        .setSaveConsumer(newValue -> BBsentials.config.bbServerURL = newValue)
                        .build());
            }
            server.addEntry(entryBuilder.startStrField(Text.of("BBsentials API key"), BBsentials.config.apiKey.replaceAll(".", "*"))
                    .setDefaultValue("unset")
                    .setTooltip(Text.of("Put you API Key here (the one generated in the Discord! with /link). §cThe Text is visually censored. Not saved unless you changed it."))
                    .setSaveConsumer((newValue) -> {
                        if (newValue.replace("*", "").trim().isEmpty()) {
                            return;
                        }
                        else {
                            BBsentials.config.apiKey = newValue;
                        }
                    })
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Connect to Test Server"), BBsentials.config.connectToBeta)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Makes you connect to the Test Server instead of the Main Server. Keep in mind that all announces may be tests and the main announces are not transferred over to here!")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> BBsentials.config.connectToBeta = newValue)
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Mojang Auth"), BBsentials.config.useMojangAuth)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Uses mojang as authenticator instead of api key"))
                    .setSaveConsumer(newValue -> BBsentials.config.useMojangAuth = newValue)
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Override Bingo Time"), BBsentials.config.overrideBingoTime)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Override the Bingo Time and connect always to the Server. (Bingo time is 14 days cause Extreme Bingo)"))
                    .setSaveConsumer(newValue -> BBsentials.config.overrideBingoTime = newValue)
                    .build());
            server.addEntry(entryBuilder.startBooleanToggle(Text.of("Allow Server Partying"), BBsentials.config.allowServerPartyInvite)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Allow the Server to party players for you automatically. (Convenience Feature. Is used for example for services to automatically party the persons which joined it)"))
                    .setSaveConsumer(newValue -> BBsentials.config.allowServerPartyInvite = newValue)
                    .build());
            //Visual
            ConfigCategory visual = builder.getOrCreateCategory(Text.of("Visual"));
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Bingo Chat"), BBsentials.config.showBingoChat)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want the Bingo Chat to be show"))
                    .setSaveConsumer(newValue -> BBsentials.config.showBingoChat = newValue)
                    .build());
            visual.addEntry(entryBuilder.startBooleanToggle(Text.of("Show Splash Status Updates"), BBsentials.config.showSplashStatusUpdates)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want to see Splash Staus updates. Keep in mind that this will only send you status updates for the Splashes which you were shown.\nThose hidden due too too high Splash Time will still remain invisible"))
                    .setSaveConsumer(newValue -> BBsentials.config.showSplashStatusUpdates = newValue)
                    .build());
            //Notifications
            ConfigCategory notifications = builder.getOrCreateCategory(Text.of("Notifications"));
            BooleanListEntry doNotifications = entryBuilder.startBooleanToggle(Text.of("Do Desktop Notifications"), BBsentials.config.doDesktopNotifications)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                    .setSaveConsumer(newValue -> BBsentials.config.doDesktopNotifications = newValue)
                    .build();
            DropdownBoxEntry<String> notificationOn = entryBuilder.startStringDropdownMenu(Text.of("Notification on"), BBsentials.config.notifForMessagesType) // Start the StringDropdownMenu entry
                    .setSelections(List.of("all", "nick", "none"))
                    .setTooltip(Text.of("When do you want to receive Desktop Notifications? on all party, messages containing nickname"))
                    .setDefaultValue("all")
                    .setRequirement(Requirement.isTrue(doNotifications))
                    .setSuggestionMode(false)
                    .setSaveConsumer(newValue -> BBsentials.config.notifForMessagesType = newValue)
                    .build();
            StringListEntry nickname = entryBuilder.startStrField(Text.of("Nickname"), BBsentials.config.nickname)
                    .setDefaultValue("")
                    .setTooltip(Text.of("Nickname. you will get send desktop notifications if a message contains one"))
                    .setRequirement(() -> {
                        return doNotifications.getValue() && notificationOn.getValue().equals("nick");
                    })
                    .setSaveConsumer(newValue -> BBsentials.config.nickname = newValue)
                    .build();

            notifications.addEntry(doNotifications);
            notifications.addEntry(notificationOn);
            notifications.addEntry(nickname);
            //other
            ConfigCategory other = builder.getOrCreateCategory(Text.of("Other"));
            other.addEntry(entryBuilder.startBooleanToggle(Text.of("Gamma Override"), BBsentials.config.doGammaOverride)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to enable full bright"))
                    .setSaveConsumer(newValue -> BBsentials.config.doGammaOverride = newValue)
                    .build());
            other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept Reparties"), BBsentials.config.acceptReparty)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                    .setSaveConsumer(newValue -> BBsentials.config.showBingoChat = newValue)
                    .build());
            other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept auto invite"), BBsentials.config.allowBBinviteMe)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Do you want that whenever someone sends you a msg ending with 'bb:party me' to send them a party invite automatically?"))
                    .setSaveConsumer(newValue -> BBsentials.config.allowBBinviteMe = newValue)
                    .build());
            SubCategoryBuilder trolls = entryBuilder.startSubCategory(Text.of("Trolls")).setExpanded(false);
            BooleanListEntry swapActionBarAndChat = (entryBuilder.startBooleanToggle(Text.of("Actionbar-Chat switch"), BBsentials.config.swapActionBarChat)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Swap that chat messages are shown in actionbar and reverse"))
                    .setSaveConsumer(newValue -> BBsentials.config.swapActionBarChat = newValue)
                    .build());
            Requirement trollSwapEnabled = swapActionBarAndChat::getValue;
            BooleanListEntry swapActionBarAndChatOnlyNormal = (entryBuilder.startBooleanToggle(Text.of("Only normal messages"), BBsentials.config.swapOnlyNormal)
                    .setDefaultValue(false)
                    .setRequirement(trollSwapEnabled)
                    .setTooltip(Text.of("Swap only the default messages (→ everything not from BBsentials)"))
                    .setSaveConsumer(newValue -> BBsentials.config.swapOnlyNormal = newValue)
                    .build());
            BooleanListEntry swapActionBarAndChatOnlyBB = (entryBuilder.startBooleanToggle(Text.of("Only BBsentials messages"), BBsentials.config.swapOnlyBBsentials)
                    .setDefaultValue(false)
                    .setRequirement(trollSwapEnabled)
                    .setTooltip(Text.of("Swap only the messages from BBsentials"))
                    .setSaveConsumer(newValue -> BBsentials.config.swapOnlyBBsentials = newValue)
                    .build());
            trolls.add(swapActionBarAndChat);
            trolls.add(swapActionBarAndChatOnlyNormal);
            trolls.add(swapActionBarAndChatOnlyBB);
            other.addEntry(trolls.build());
            ConfigCategory chChestItems = builder.getOrCreateCategory(Text.of("Ch Chest Items"));
            {
                BooleanListEntry allItems = entryBuilder.startBooleanToggle(Text.of("All Chest Items"), BBsentials.config.toDisplayConfig.allChChestItem)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Select to receive notifications when an any Item is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.allChChestItem = newValue)
                        .build();
                chChestItems.addEntry(allItems);
                Requirement notAllItemsRequirement = () -> !allItems.getValue();
                chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("ALL Robo Parts "), BBsentials.config.toDisplayConfig.allRoboPart)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when an allRoboPartCustomChChestItem is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.allRoboPart = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());
                BooleanListEntry allRoboParts = (entryBuilder.startBooleanToggle(Text.of("Custom (Other) Items"), BBsentials.config.toDisplayConfig.customChChestItem)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when any not already defined Item is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.customChChestItem = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());
                chChestItems.addEntry(allRoboParts);
                Requirement notAllRoboPartsRequirement = () -> !allRoboParts.getValue();
                chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Prehistoric Egg"), BBsentials.config.toDisplayConfig.prehistoricEgg)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Prehistoric Egg is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.prehistoricEgg = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());

                chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Pickonimbus 2000"), BBsentials.config.toDisplayConfig.pickonimbus2000)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Pickonimbus 2000 is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.pickonimbus2000 = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());
                SubCategoryBuilder roboParts = entryBuilder.startSubCategory(Text.of("Robo Parts")).setRequirement(Requirement.all(notAllRoboPartsRequirement, notAllItemsRequirement)).setExpanded(true);
                roboParts.add(entryBuilder.startBooleanToggle(Text.of("Control Switch"), BBsentials.config.toDisplayConfig.controlSwitch)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Control Switch is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.controlSwitch = newValue)
                        .build());

                roboParts.add(entryBuilder.startBooleanToggle(Text.of("Electron Transmitter"), BBsentials.config.toDisplayConfig.electronTransmitter)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when an Electron Transmitter is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.electronTransmitter = newValue)
                        .build());

                roboParts.add(entryBuilder.startBooleanToggle(Text.of("FTX 3070"), BBsentials.config.toDisplayConfig.ftx3070)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a FTX 3070 is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.ftx3070 = newValue)
                        .build());

                roboParts.add(entryBuilder.startBooleanToggle(Text.of("Robotron Reflector"), BBsentials.config.toDisplayConfig.robotronReflector)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Robotron Reflector is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.robotronReflector = newValue)
                        .build());

                roboParts.add(entryBuilder.startBooleanToggle(Text.of("Superlite Motor"), BBsentials.config.toDisplayConfig.superliteMotor)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Superlite Motor is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.superliteMotor = newValue)
                        .build());

                roboParts.add(entryBuilder.startBooleanToggle(Text.of("Synthetic Heart"), BBsentials.config.toDisplayConfig.syntheticHeart)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a Synthetic Heart is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.syntheticHeart = newValue)
                        .build());
                chChestItems.addEntry(roboParts.build());
                chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Flawless Gemstone"), BBsentials.config.toDisplayConfig.flawlessGemstone)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when any Flawless Gemstone is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.flawlessGemstone = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());
                chChestItems.addEntry(entryBuilder.startBooleanToggle(Text.of("Jungle Heart"), BBsentials.config.toDisplayConfig.jungleHeart)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Select to receive notifications when a JungleHeart is found"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.jungleHeart = newValue)
                        .setRequirement(notAllItemsRequirement)
                        .build());
            }//CHChestItems
            ConfigCategory miningEvents = builder.getOrCreateCategory(Text.of("Mining Events"));
            {
                BooleanListEntry allEvents = entryBuilder.startBooleanToggle(Text.of("All Events"), BBsentials.config.toDisplayConfig.allEvents)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Get updated for any Mining Event"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.allEvents = newValue)
                        .build();
                miningEvents.addEntry(allEvents);
                miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("§cBlock Crystal Hollow Events"), BBsentials.config.toDisplayConfig.blockChEvents)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Block getting Crystal Hollow Events. Maybe if you haven't accessed Crystal Hollows yet "))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.blockChEvents = newValue)
                        .build());
                miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Gone with the Wind"), BBsentials.config.toDisplayConfig.goneWithTheWind) // Start the StringDropdownMenu entry
                        .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                        .setTooltip(Text.of("Get notified when a Gone with the Wind happens in the specified Island"))
                        .setDefaultValue("none")
                        .setSuggestionMode(false)
                        .setRequirement(() -> !allEvents.getValue())
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.goneWithTheWind = newValue)
                        .build());
                miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Better Together"), BBsentials.config.toDisplayConfig.betterTogether) // Start the StringDropdownMenu entry
                        .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                        .setTooltip(Text.of("Get notified when a Better Together happens in the specified Island"))
                        .setDefaultValue("none")
                        .setSuggestionMode(false)
                        .setRequirement(() -> !allEvents.getValue())
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.betterTogether = newValue)
                        .build());
                miningEvents.addEntry(entryBuilder.startStringDropdownMenu(Text.of("Double Powder"), BBsentials.config.toDisplayConfig.doublePowder) // Start the StringDropdownMenu entry
                        .setSelections(List.of("both", Islands.DWARVEN_MINES.getDisplayName(), Islands.CRYSTAL_HOLLOWS.getDisplayName(), "none"))
                        .setTooltip(Text.of("Get notified when a Double Powder happens in the specified Island"))
                        .setDefaultValue("none")
                        .setRequirement(() -> !allEvents.getValue())
                        .setSuggestionMode(false)
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.doublePowder = newValue)
                        .build());
                miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Mithril Gourmand"), BBsentials.config.toDisplayConfig.mithrilGourmand)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Get notified when a Mithril Gourmand happens"))
                        .setRequirement(() -> !allEvents.getValue())
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.mithrilGourmand = newValue)
                        .build());
                miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Raffle"), BBsentials.config.toDisplayConfig.raffle)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Get notified when a Raffle happens"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.raffle = newValue)
                        .setRequirement(() -> !allEvents.getValue())
                        .build());
                miningEvents.addEntry(entryBuilder.startBooleanToggle(Text.of("Goblin Raid"), BBsentials.config.toDisplayConfig.goblinRaid)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Get notified when a Goblin Raid happens"))
                        .setSaveConsumer(newValue -> BBsentials.config.toDisplayConfig.goblinRaid = newValue)
                        .setRequirement(() -> !allEvents.getValue())
                        .build());
            } //Mining Events
            if (BBsentials.config.hasBBRoles("dev")) {
                ConfigCategory dev = builder.getOrCreateCategory(Text.of("§3Developing"));
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Mode"), BBsentials.config.devMode)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Dev Mode"))
                        .setSaveConsumer(newValue -> BBsentials.config.devMode = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Detailed Dev Mode"), BBsentials.config.detailedDevMode)
                        .setDefaultValue(false)
                        .setTooltip(Text.of("Detailed Dev Mode"))
                        .setSaveConsumer(newValue -> BBsentials.config.detailedDevMode = newValue)
                        .build());
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Dev Security"), BBsentials.config.devSecurity)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Shows dev debug even when its sensetive information"))
                        .setSaveConsumer(newValue -> BBsentials.config.devSecurity = newValue)
                        .build());
            }
            if (BBsentials.config.hasBBRoles("splasher")) {
                ConfigCategory dev = builder.getOrCreateCategory(Text.of("§dSplashes"));
                dev.addEntry(entryBuilder.startBooleanToggle(Text.of("Auto Update Statuses"), BBsentials.config.autoSplashStatusUpdates)
                        .setDefaultValue(true)
                        .setTooltip(Text.of("Automatically updates the Status of the Splash by sending packets to the Server"))
                        .setSaveConsumer(newValue -> BBsentials.config.autoSplashStatusUpdates = newValue)
                        .build());
            }

            return builder.build();
        }
    }
}
