package dmillerw.lore.item;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.network.PacketHandler;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
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
		setCreativeTab(CreativeTabs.tabBrewing);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
		if (!world.isRemote && entity instanceof EntityPlayer) {
			if (stack.getItemDamage() > 0) {
				int dimension = entity.worldObj.provider.dimensionId;
				LoreData data = LoreLoader.INSTANCE.getLore(stack.getItemDamage());

				if (data == null || data.contents.isEmpty()) {
					LoreExpansion.logger.warn("Found item with invalid lore ID. Resetting");
					stack.setItemDamage(0);
				}

				List<Integer> list = PlayerHandler.getLore((EntityPlayer) entity);

				if (list == null) {
					list = new ArrayList<Integer>();
				}

				if (!list.contains(stack.getItemDamage())) {
					list.add(stack.getItemDamage());
				}
				PlayerHandler.setLore((EntityPlayer) entity, list);
				PacketHandler.INSTANCE.sendToAll(new PacketSyncLore((EntityPlayer) entity, list));

				((EntityPlayer)entity).inventory.setInventorySlotContents(slot, null);
				((EntityPlayerMP)entity).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, slot, null));
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
		if (stack.getItemDamage() > 0) {
			int dimension = player.worldObj.provider.dimensionId;
			LoreData data = LoreLoader.INSTANCE.getLore(stack.getItemDamage());

			if (data == null || data.contents.isEmpty()) {
				LoreExpansion.logger.warn("Found item with invalid lore ID. Resetting");
				stack.setItemDamage(0);
			}

			if (data != null && data.validForDimension(dimension)) {
				list.add(data.getTitle(dimension));
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (LoreData data : LoreLoader.INSTANCE.getLore()) {
			if (data != null) {
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
