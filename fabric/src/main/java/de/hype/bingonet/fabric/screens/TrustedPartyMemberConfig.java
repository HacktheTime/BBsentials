package de.hype.bingonet.fabric.screens;

import de.hype.bingonet.client.common.client.objects.TrustedPartyMember;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TrustedPartyMemberConfig {
    public static Screen create(Screen parent, TrustedPartyMember data) {
        String originalUsername = data.getUsername();
        String originalUUID = data.mcUuid.replace("-", "");

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> data.save(originalUsername, originalUUID))
                .setTitle(Text.of("Trusted Party Member"));
        Text text = Text.literal("");
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory main = builder.getOrCreateCategory(Text.literal(data.getUsername()));

        StringListEntry username = entryBuilder.startTextField(Text.of("Username"), originalUsername)
                .setDefaultValue(text.getString())
                .setSaveConsumer(data::setUsernameOverwrite)
                .setTooltip(Text.literal("The Username of the person"))
                .build();
        main.addEntry(username);
        main.addEntry(entryBuilder.startTextField(Text.of("UUID"), originalUUID)
                .setDefaultValue(text.getString())
                .setSaveConsumer((value) -> {
                    data.mcUuid = value.replace("-", "");
                })
                .setTooltip(Text.literal("The UUID of the person"))
                .setRequirement(Requirement.isTrue(() -> !username.getValue().equals(originalUsername)))
                .build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Party Admin"), data.partyAdmin()).setDefaultValue(false).setSaveConsumer(data::partyAdmin).setTooltip(Text.literal("All Permissions but also promote, transfer, demote,...")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Invite"), data.canInvite()).setDefaultValue(false).setSaveConsumer(data::canInvite).setTooltip(Text.literal("Whether you want them to be allowed to invite people")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Kick"), data.canKick()).setDefaultValue(false).setSaveConsumer(data::canKick).setTooltip(Text.literal("Whether you want them to be able to kick people")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Ban"), data.canBan()).setDefaultValue(false).setSaveConsumer(data::canBan).setTooltip(Text.literal("Enable to allow the person to kick a member from the party and add them to the ignore list afterwards to effectively ban them")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Mute"), data.canMute()).setDefaultValue(false).setSaveConsumer(data::canMute).setTooltip(Text.literal("Whether you want them to be able to mute the party")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Trigger Warp"), data.canRequestWarp()).setDefaultValue(false).setSaveConsumer(data::canRequestWarp).setTooltip(Text.literal("Whether you want them to be able to trigger party warps.")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Trigger Polls"), data.canRequestPolls()).setDefaultValue(false).setSaveConsumer(data::canRequestPolls).setTooltip(Text.literal("Whether you want them to be able to make polls in your name. Keep in mind that polls require §6MVP++§r")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Can Kick Offline"), data.canRequestPolls()).setDefaultValue(false).setSaveConsumer(data::canRequestPolls).setTooltip(Text.literal("Whether you want them to be able to kick offline members")).build());
        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
