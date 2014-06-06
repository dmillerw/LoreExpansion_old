package dmillerw.lore.item;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.core.GuiHandler;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.network.PacketHandler;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ItemJournal extends Item {

	public ItemJournal() {
		super();

		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.openGui(LoreExpansion.instance, GuiHandler.GUI_JOURNAL, world, 0, 0, 0);
		return stack;
	}
}
