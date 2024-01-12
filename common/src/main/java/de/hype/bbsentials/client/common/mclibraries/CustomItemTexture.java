package de.hype.bbsentials.client.common.mclibraries;

import de.hype.bbsentials.client.common.client.BBsentials;

public abstract class CustomItemTexture {
    public static int customTextureId = 0;
    public String nameSpace = null;
    public String renderTextureId = null;
    public int textureId = customTextureId++;

    /**
     * @param nameSpace
     * @param renderTextureId keep in mind that the texture probably needs to be in Texturepackfolder/assets/bbsentials/textures/gui/sprites/likeyouwant
     */
    public CustomItemTexture(String nameSpace, String renderTextureId) {
        this.nameSpace = nameSpace;
        this.renderTextureId = renderTextureId;
        addToPool();
    }

    /**
     * @param renderTextureId only id NO NAMEPSACE!
     */
    public CustomItemTexture(String renderTextureId) {
        this.nameSpace = "bbsentials";
        this.renderTextureId = renderTextureId;
        addToPool();
    }

    protected int addToPool() {
        BBsentials.customItemTextures.put(textureId, this);
        return textureId;
    }

    public CustomItemTexture removeFromPool() {
        return BBsentials.customItemTextures.remove(textureId);
    }

    /**
     * @param itemName
     * @param tooltip  Tooltip is at the moment still empty. Not supported yet
     * @param nbt
     * @param item
     * @return
     */
    public abstract boolean isItem(String itemName, String tooltip, String nbt, Object item);
}
