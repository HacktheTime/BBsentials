package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.objects.RouteNode;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class RouteNodeConfigScreen {
    public static Screen create(Screen parent, RouteNode data) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("BBsentials Waypoint Config"));
        Text text = Text.literal("");
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory main = builder.getOrCreateCategory(Text.literal("Node"));

        main.addEntry(entryBuilder.startTextField(Text.of("Name"), text.getString())
                .setDefaultValue(text.getString())
                .setSaveConsumer((value) -> {
                    data.name= value;
                })
                .setTooltip(Text.literal("The Name will only be updated when you change it. Formatting is lost in display here but is not lost normally."))
                .build());
        main.addEntry(entryBuilder.startColorField(Text.literal("Render Color"), Color.ofRGB(data.color.getRed(), data.color.getGreen(), data.color.getBlue()))
                .setSaveConsumer((value) -> data.color = new java.awt.Color(value)).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("X:"), data.coords.x).setDefaultValue(data.coords.x).setSaveConsumer((newx) -> data.coords.x = newx).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Y:"), data.coords.y).setDefaultValue(data.coords.y).setSaveConsumer((newy) -> data.coords.y = newy).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Z:"), data.coords.z).setDefaultValue(data.coords.z).setSaveConsumer((newz) -> data.coords.z = newz).build());
        main.addEntry(entryBuilder.startIntField(Text.literal("Trigger Next Distance:"), data.triggerNextRange).setDefaultValue(-1).setSaveConsumer((newvalue) -> data.triggerNextRange = newvalue).setTooltip(Text.literal("Distance to the point that will trigger to hide this waypoint and show the next one.")).build());
        try {
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
