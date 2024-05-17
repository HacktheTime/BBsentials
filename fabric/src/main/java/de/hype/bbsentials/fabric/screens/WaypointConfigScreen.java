package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.TextUtils;
import de.hype.bbsentials.fabric.FabricTextUtils;
import de.hype.bbsentials.shared.objects.WaypointData;
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
                .setTitle(Text.of("BBsentials Waypoint Config"));
        Text text = Text.literal("");
        try {
            text = FabricTextUtils.jsonToText(data.jsonToRenderText);
        } catch (Exception ignored) {

        }
        Text finalText = text;
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory main = builder.getOrCreateCategory(Text.literal("Waypoint"));

        main.addEntry(entryBuilder.startTextField(Text.of("Name"), text.getString())
                .setDefaultValue(text.getString())
                .setSaveConsumer((value) -> {
                    if (value.equals(finalText.getString())) return;
                    data.jsonToRenderText = FabricTextUtils.literalJson(value.replace("&", "§"));
                })
                .setTooltip(Text.literal("The Name will only be updated when you change it. Formatting is lost in display here but is not lost normally."))
                .build());
        main.addEntry(entryBuilder.startColorField(Text.literal("Render Color"), Color.ofRGB(data.color.getRed(), data.color.getGreen(), data.color.getBlue()))
                .setSaveConsumer((value) -> data.color = new java.awt.Color(value)).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("X:"), data.position.x).setDefaultValue(data.position.x).setSaveConsumer((newx) -> data.position.x = newx).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Y:"), data.position.y).setDefaultValue(data.position.y).setSaveConsumer((newy) -> data.position.y = newy).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Z:"), data.position.z).setDefaultValue(data.position.z).setSaveConsumer((newz) -> data.position.z = newz).build());
        //Issue is the accuracy limitation in bits for float basically Integer overflow problem like
        main.addEntry(entryBuilder.startIntField(Text.literal("Render Distance:"), data.renderDistance).setDefaultValue(10000).setSaveConsumer((newvalue) -> data.renderDistance = newvalue).setTooltip(Text.literal("Maximum Distance for the waypoint to be rendered. \n The System is not able to display Waypoint over large distances due too technical limitations. Do not ask for help if you increase this number.")).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Visible"), data.visible).setDefaultValue(true).setSaveConsumer(newValue -> data.visible = newValue).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Delete on Server Swap"), data.deleteOnServerSwap).setDefaultValue(true).setSaveConsumer(newValue -> data.deleteOnServerSwap = newValue).build());
        main.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show Tracer"), data.doTracer).setTooltip(Text.of("Show Tracers to the Waypoint?\nThis will render a line to the waypoint on your screen. Default can be changed in the Visual Config")).setDefaultValue(BBsentials.visualConfig.waypointDefaultWithTracer).setSaveConsumer(newValue -> data.doTracer = newValue).build());
        //TODO do the custom textures for waypoints
        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
