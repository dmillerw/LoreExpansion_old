package dmillerw.lore.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.command.LoreCommandSender;
import dmillerw.lore.item.ItemLoreScrap;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketNotification;
import dmillerw.lore.network.PacketSyncLore;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

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
						LoreKey key = ItemLoreScrap.getLore(stack);
						if (key != null) {
							Lore data = LoreLoader.INSTANCE.getLore(key);

							if (data == null) {
								LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
								stack.setTagCompound(new NBTTagCompound());
								return;
							}

							List<LoreKey> list = PlayerHandler.getLore(event.player);

							if (!list.contains(key)) {
								list.add(key);
							}
							PlayerHandler.setLore(event.player, list);
							PacketSyncLore.updateLore((EntityPlayerMP) event.player);
							PacketNotification.notify((EntityPlayerMP) event.player, key.page, key.dimension, PacketNotification.PICKUP);

							CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
							LoreCommandSender commandSender = new LoreCommandSender(event.player);
							for (String str : data.commands) {
								ch.executeCommand(commandSender, str);
							}

							if (!notifiedThisTick) {
								event.player.addChatComponentMessage(new ChatComponentText("You've discovered a new lore page. Press " + Keyboard.getKeyName(KeyHandler.INSTANCE.key.getKeyCode()) + " to view"));
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
