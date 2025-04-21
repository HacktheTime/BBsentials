package de.hype.bingonet.fabric.screens;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.TextUtils;
import de.hype.bingonet.fabric.FabricTextUtils;
import de.hype.bingonet.shared.objects.WaypointData;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WaypointConfigScreen {
    public static Screen create(Screen parent, WaypointData data) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Bingo Net Waypoint Config"));
        Text text = Text.literal("");
        try {
            text = FabricTextUtils.jsonToText(data.getJsonToRenderText());
        } catch (Exception ignored) {

        }
        Text finalText = text;
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory main = builder.getOrCreateCategory(Text.literal("Waypoint"));

        main.addEntry(entryBuilder.startTextField(Text.of("Name"), text.getString())
                .setDefaultValue(text.getString())
                .setSaveConsumer((value) -> {
                    if (value.equals(finalText.getString())) return;
                    data.setJsonToRenderText(FabricTextUtils.literalJson(value.replace("&", "§")));
                })
                .setTooltip(Text.literal("The Name will only be updated when you change it. Formatting is lost in display here but is not lost normally."))
                .build());
        main.addEntry(entryBuilder.startColorField(Text.literal("Render Color"), Color.ofRGB(data.getColor().getRed(), data.getColor().getGreen(), data.getColor().getBlue()))
                .setSaveConsumer((value) -> data.setColor(new java.awt.Color(value))).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("X:"), data.getPosition().x).setDefaultValue(data.getPosition().x).setSaveConsumer((newx) -> data.getPosition().x = newx).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Y:"), data.getPosition().y).setDefaultValue(data.getPosition().y).setSaveConsumer((newy) -> data.getPosition().y = newy).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Z:"), data.getPosition().z).setDefaultValue(data.getPosition().z).setSaveConsumer((newz) -> data.getPosition().z = newz).build());
        //Issue is the accuracy limitation in bits for float basically Integer overflow problem like
        main.addEntry(entryBuilder.startIntField(Text.literal("Render Distance:"), data.getRenderDistance()).setDefaultValue(10000).setSaveConsumer(data::setRenderDistance).setTooltip(Text.literal("Maximum Distance for the waypoint to be rendered. \n The System is not able to display Waypoint over large distances due too technical limitations. Do not ask for help if you increase this number.")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Visible"), data.getVisible()).setDefaultValue(true).setSaveConsumer(data::setVisible).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Delete on Server Swap"), data.getDeleteOnServerSwap()).setDefaultValue(true).setSaveConsumer(data::setDeleteOnServerSwap).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Tracer"), data.getDoTracer()).setTooltip(Text.of("Show Tracers to the Waypoint?\nThis will render a line to the waypoint on your screen. Default can be changed in the Visual Config")).setDefaultValue(BingoNet.visualConfig.waypointDefaultWithTracer).setSaveConsumer(data::setDoTracer).build());
        //TODO do the custom textures for waypoints
        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
