package de.hype.bbsentials.shared.objects;

public class RenderInformation {
    public String namespace = "";
    public String pathToFile = "";
    public Integer spaceToNext = 5;

    /**
     * @param namespace       namespace like bbsentials
     * @param textureFilePath path to file from assets fully with file ending → example: textures/gui/sprites/customitems/splash_hub.png
     */
    public RenderInformation(String namespace, String textureFilePath) {
        this.namespace = namespace;
        this.pathToFile = textureFilePath;
        if (namespace == null) {
            this.namespace = "";
        }
        if (textureFilePath == null) {
            this.pathToFile = "";
        }
    }

    public RenderInformation(String namespace, String textureFilePath, int spaceToNext) {
        this.namespace = namespace;
        this.pathToFile = textureFilePath;
        this.spaceToNext = spaceToNext;
        if (namespace == null) {
            this.namespace = "";
        }
        if (textureFilePath == null) {
            this.pathToFile = "";
        }
    }

    public String getTexturePath() {
        if (namespace.isEmpty()) return pathToFile;
        return namespace + ":" + pathToFile;
    }
}
