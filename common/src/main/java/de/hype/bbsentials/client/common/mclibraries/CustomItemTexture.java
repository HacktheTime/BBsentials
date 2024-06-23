package de.hype.bbsentials.client.common.mclibraries;

public abstract class CustomItemTexture {
    public static int customTextureId = 0;
    public String nameSpace = null;
    public String renderTextureId = null;
    public int textureId = customTextureId++;
    public int poolId;
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

    protected void addToPool() {
//        poolId = EnvironmentCore.utils.addRender(this);
    }

    public void removeFromPool() {
//        EnvironmentCore.utils.removeRenderId(this.textureId);
    }
//
//    /**
//     * @param itemName
//     * @param nbt
//     * @return
//     */
//    public boolean isItem(String itemName, String nbt);

    /**
     * Keep in mind that there is also a full method for which you can use either
     *
     * @param itemName
     * @return
     */
    public abstract boolean isItem(String itemName);
}
