package de.hype.bingonet.client.common.mclibraries.interfaces;

import java.util.List;
import java.util.function.Predicate;

public class CustomRenderTexture {
    private static Integer customTextureID = 0;
    String texture;
    Integer selfId = customTextureID++;

    CustomRenderTexture(String texture) {
        this.texture = texture;
    }


    public String getTexture() {
        return texture;
    }

    public static class Name extends CustomRenderTexture {

        Name(String texture, Predicate<String> namemates) {
            super(texture);
        }
    }

    public static class Advanced extends CustomRenderTexture {

        Advanced(String texture, Predicate<String> name, Predicate<List<Text>> tooltipMatches) {
            super(texture);
        }
    }
}