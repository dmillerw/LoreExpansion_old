package dmillerw.lore.item;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.core.TabLore;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemLoreScrap extends Item {

	public static void setLore(ItemStack stack, LoreKey key) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		nbt.setInteger("page", key.page);
		nbt.setInteger("dimension", key.dimension);
		stack.setTagCompound(nbt);
	}

	public static LoreKey getLore(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}
		NBTTagCompound nbt = stack.getTagCompound();
		int page = nbt.getInteger("page");
		int dimension = nbt.getInteger("dimension");
		return new LoreKey(page, dimension);
	}

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
				LoreKey key = ItemLoreScrap.getLore(stack);
				if (key != null) {
					Lore data = LoreLoader.INSTANCE.getLore(key);

					if (data == null) {
						LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
						stack.setTagCompound(new NBTTagCompound());
					}

					List<LoreKey> list = PlayerHandler.getLore(player);

					if (!list.contains(key)) {
						list.add(key);
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
		LoreKey key = ItemLoreScrap.getLore(stack);
		if (key != null) {
			int dimension = player.worldObj.provider.dimensionId;
			Lore data = LoreLoader.INSTANCE.getLore(key);

			if (data == null) {
				LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
				stack.setTagCompound(new NBTTagCompound());
			}

			if (data != null) {
				list.add(String.format("Page %s: %s", key.page, data.title));
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		int dimension = FMLClientHandler.instance().getClient().theWorld.provider.dimensionId;
		for (Lore data : LoreLoader.INSTANCE.getAllLore()) {
			if (data != null && data.validDimension(dimension)) {
				ItemStack stack = new ItemStack(this);
				ItemLoreScrap.setLore(stack, new LoreKey(data));
				list.add(stack);
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
