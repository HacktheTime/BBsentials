package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.TrustedPartyMember;
import de.hype.bbsentials.client.common.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

public class TrustedPartyMembersConfigScreen extends SelectionScreen<TrustedPartyMember> {
    public TrustedPartyMembersConfigScreen(Screen parent) {
        super(parent, "Trusted Party Members");
    }

    public static void openFromNothing() {
        TrustedPartyMembersConfigScreen sc = new TrustedPartyMembersConfigScreen(MinecraftClient.getInstance().currentScreen);
        sc.updateFields();
        sc.setScreen(sc);
    }

    @Override
    public List<TrustedPartyMember> getObjectList() {
        return BBsentials.partyConfig.trustedPartyMembers;
    }

    @Override
    protected void addNewRow() {
        BBsentials.partyConfig.trustedPartyMembers.add(new TrustedPartyMember("", ""));
    }

    /**
     * What do you want to happen when the button is clicked?
     *
     * @param object       the object the button is initialised with.
     * @param buttonWidget the button itself
     * @return what shall happen when the button is pressed
     */
    @Override
    public void doOnButtonClick(TrustedPartyMember object, ButtonWidget buttonWidget) {
        setScreen(TrustedPartyMemberConfig.create(this, object));
    }

    @Override
    public String getButtonString(TrustedPartyMember object) {
        return object.toString();
    }

    @Override
    void removeRow(TrustedPartyMember node) {
        List<TrustedPartyMember> members = BBsentials.partyConfig.trustedPartyMembers;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).mcUuid.equals(node.mcUuid)) {
                members.remove(i);
                updateFields();
                return;
            }
        }
    }

    @Override
    public void done() {
        ConfigManager.saveAll();
        close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        super.doDefaultClose();
    }
}
