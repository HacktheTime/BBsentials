package de.hype.bingonet.client.common.mclibraries.interfaces;


import de.hype.bingonet.shared.constants.VanillaItems;

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
