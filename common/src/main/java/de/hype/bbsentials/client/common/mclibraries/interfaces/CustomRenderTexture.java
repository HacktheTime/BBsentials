package de.hype.bbsentials.client.common.mclibraries.interfaces;

import de.hype.bbsentials.client.common.Text;

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

    public abstract static class Name extends CustomRenderTexture {

        Name(String texture, Predicate<String> namemates) {
            super(texture);
        }
    }

    public abstract static class Advanced extends CustomRenderTexture {

        Advanced(String texture, Predicate<String> name, Predicate<List<Text>> tooltipMatches) {
            super(texture);
        }
    }
}