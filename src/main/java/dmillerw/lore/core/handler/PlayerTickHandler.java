package dmillerw.lore.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.network.PacketHandler;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class PlayerTickHandler {

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		boolean notifiedThisTick = false;
		if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
			if (!event.player.capabilities.isCreativeMode) {
				for (int i=0; i<event.player.inventory.getSizeInventory(); i++) {
					ItemStack stack = event.player.inventory.getStackInSlot(i);

					if (stack != null && stack.getItem() == LoreExpansion.loreScrap) {
						if (stack.getItemDamage() > 0) {
							LoreData data = LoreLoader.INSTANCE.getLore(stack.getItemDamage());

							if (data == null || data.contents.isEmpty()) {
								LoreExpansion.logger.warn("Found item with invalid lore ID. Resetting");
								stack.setItemDamage(0);
								return;
							}

							List<Integer> list = PlayerHandler.getLore(event.player);

							if (list == null) {
								list = new ArrayList<Integer>();
							}

							if (!list.contains(stack.getItemDamage())) {
								list.add(stack.getItemDamage());
							}
							PlayerHandler.setLore(event.player, list);
							PacketHandler.INSTANCE.sendToAll(new PacketSyncLore(event.player, list));

							if (!notifiedThisTick) {
								event.player.addChatComponentMessage(new ChatComponentText("You've discovered a new lore page. Press " + Keyboard.getKeyName(KeyHandler.INSTANCE.key.getKeyCode()) + " to open your journal"));
								notifiedThisTick = true;
							}

							event.player.inventory.setInventorySlotContents(i, null);
							event.player.inventory.markDirty();
						}
					}
				}
			}
		}
	}

}
