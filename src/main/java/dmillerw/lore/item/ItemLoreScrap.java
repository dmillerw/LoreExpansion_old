package dmillerw.lore.item;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.core.TabLore;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class ItemLoreScrap extends Item {

	private IIcon icon;

	public ItemLoreScrap() {
		super();

		setMaxDamage(0);
		setHasSubtypes(true);
		setMaxStackSize(1);
		setCreativeTab(TabLore.TAB);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.capabilities.isCreativeMode) {
				if (stack.getItemDamage() > 0) {
					LoreData data = LoreLoader.INSTANCE.getLore(stack.getItemDamage());

					if (data == null || (data.contents.isEmpty() && !data.global)) {
						LoreExpansion.logger.warn("Found item with invalid lore ID. Resetting");
						stack.setItemDamage(0);
					}

					List<Integer> list = PlayerHandler.getLore(player);

					if (list == null) {
						list = new ArrayList<Integer>();
					}

					if (!list.contains(stack.getItemDamage())) {
						list.add(stack.getItemDamage());
					}
					PlayerHandler.setLore(player, list);
					PacketSyncLore.updateLore((EntityPlayerMP) player);

					player.addChatComponentMessage(new ChatComponentText("Added lore page #" + data.page));
				}
			}
		}

		return stack;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
		if (stack.getItemDamage() > 0) {
			int dimension = player.worldObj.provider.dimensionId;
			LoreData data = LoreLoader.INSTANCE.getLore(stack.getItemDamage());

			if (data == null || (data.contents.isEmpty() && !data.global)) {
				LoreExpansion.logger.warn("Found item with invalid lore ID. Resetting");
				stack.setItemDamage(0);
			}

			if (data != null && data.validForDimension(dimension)) {
				list.add(String.format("Page %s: %s", stack.getItemDamage(), data.getTitle(dimension)));
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		int dimension = FMLClientHandler.instance().getClient().theWorld.provider.dimensionId;
		for (LoreData data : LoreLoader.INSTANCE.getLore()) {
			if (data != null && data.validForDimension(dimension) && data.hasLore(dimension)) {
				list.add(new ItemStack(this, 1, data.page));
			}
		}
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon("loreexp:scrap");
	}
}
