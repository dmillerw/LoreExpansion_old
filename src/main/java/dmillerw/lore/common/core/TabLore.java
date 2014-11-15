package dmillerw.lore.common.core;

import dmillerw.lore.LoreExpansion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class TabLore extends CreativeTabs {

    public static final CreativeTabs TAB = new TabLore();

    public TabLore() {
        super("loreexp");
    }

    @Override
    public Item getTabIconItem() {
        return LoreExpansion.journal;
    }
}
