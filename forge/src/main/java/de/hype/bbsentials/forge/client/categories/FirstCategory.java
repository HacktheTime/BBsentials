package de.hype.bingonet.forge.client.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.ChromaColour;
import io.github.moulberry.moulconfig.annotations.*;

public class FirstCategory {
    @Expose
    @ConfigOption(name = "ConfigEditorDropdown", desc = "ConfigEditorDropdown")
    @ConfigEditorDropdown(values = {"hi", "test"})
    public String test;

    @Expose
    @ConfigOption(name = "First Toggle", desc = "Enable this toggle to activate a feature.")
    @ConfigEditorBoolean
    public boolean firstToggle = false;

    @Expose
    @ConfigOption(name = "Color Editor", desc = "Color Editor")
    @ConfigEditorColour
    public String color = ChromaColour.fromRGB(255, 0, 0, 1000, 255).toLegacyString();

    @Expose
    @ConfigOption(name = "ConfigEditorInfoText", desc = "Info Text Sample")
    @ConfigEditorInfoText(infoTitle = "§6Info Text Sample")
    public String text = "";

    @Expose
    @ConfigOption(name = "Text Example", desc = "Text Sample")
    @ConfigEditorText
    public String textSample = "";

    @Expose
    @ConfigOption(name = "Slider Example", desc = "Slider Sample")
    @ConfigEditorSlider(minStep = 10, maxValue = 100, minValue = 0)
    public int i = 0;

//    @Expose
//    @ConfigOption(name = "Keybinding Example", desc = "Keybinding Sample")
//    @ConfigEditorKeybind(defaultKey = 12)
//    public ConfigEditorKeybind keybind = new KeyBinding("Craft",12,"");
//
//    @Expose
//    @ConfigOption(name = "Button Example", desc = "Button Sample")
//    @ConfigEditorButton
//    public ConfigEditorButton button = new ConfigEditorButton();
//    @Expose
//    @ConfigOption(name = "Draggable List Example", desc = "Draggable List Sample")
//    @ConfigEditorDraggableList
//    public ConfigEditorDraggableList draggableList = new ConfigEditorDraggableList();


}
