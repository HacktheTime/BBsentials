package de.hype.bbsentials.client;

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
        server.addEntry(entryBuilder.startStrField(Text.of("Server URL"), config.getBBServerURL())
                .setDefaultValue("localhost")
                .setTooltip(Text.of("Place the Server URL of the BBsentials Server here"))
                .setSaveConsumer(newValue -> config.bbServerURL = newValue)
                .build());
        server.addEntry(entryBuilder.startStrField(Text.of("BBsentials API key"), config.apiKey)
                .setDefaultValue("unset")
                .setTooltip(Text.of("Put you API Key here (the one generated in the Discord! with /link)"))
                .setSaveConsumer(newValue -> config.apiKey = newValue)
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
                .setSuggestionMode(true)
                .setSaveConsumer(newValue -> config.NotifForPartyMessagesType = newValue)
                .build());
        //other
        ConfigCategory other = builder.getOrCreateCategory(Text.of("Other"));
        other.addEntry(entryBuilder.startBooleanToggle(Text.of("Accept Reparties"), config.acceptReparty)
                .setDefaultValue(true)
                .setTooltip(Text.of("Select if you want BBsentials to automatically accept reparties"))
                .setSaveConsumer(newValue -> config.showBingoChat = newValue)
                .build());



        return builder.build();
    }
}
