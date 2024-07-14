package de.hype.bbsentials.client.common.mclibraries.interfaces;


import de.hype.bbsentials.shared.constants.VanillaItems;

import java.util.List;

public interface ItemStack {
    Text getName();

    void setName(Text value);

    List<Text> getTooltip();

    NBTCompound getCustomData();

    List<Text> getItemLore();

    Integer getCount();

    VanillaItems getItem();
}
