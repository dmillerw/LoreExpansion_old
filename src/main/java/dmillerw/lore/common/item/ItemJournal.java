package dmillerw.lore.common.item;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.core.GuiHandler;
import dmillerw.lore.common.core.TabLore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ItemJournal extends Item {

	private IIcon icon;

	public ItemJournal() {
		super();

		setMaxStackSize(1);
		setCreativeTab(TabLore.TAB);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.openGui(LoreExpansion.instance, GuiHandler.GUI_JOURNAL, world, 0, 0, 0);
		return stack;
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon("loreexp:journal");
	}
}
