package dmillerw.lore.common.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.command.LoreCommandSender;
import dmillerw.lore.common.item.ItemLorePage;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.Commands;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.entity.LoreProperties;
import dmillerw.lore.common.network.packet.PacketClientNotification;
import dmillerw.lore.common.network.packet.PacketHandler;
import dmillerw.lore.common.network.packet.PacketSyncLore;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

/**
 * @author dmillerw
 */
public class PlayerTickHandler {

    private boolean notifiedThisTick = false;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
            if (!event.player.capabilities.isCreativeMode) {
                for (int i = 0; i < event.player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = event.player.inventory.getStackInSlot(i);

                    if (stack != null && stack.getItem() == LoreExpansion.lorePage) {
                        LoreKey key = ItemLorePage.getLore(stack);
                        if (key != null) {
                            Lore lore = LoreLoader.INSTANCE.getLore(key);

                            if (lore == null) {
                                LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
                                stack.setTagCompound(new NBTTagCompound());
                                return;
                            }

                            LoreProperties collectedLore = PlayerHandler.getCollectedLore(event.player);
                            if (!collectedLore.hasLore(key)) {
                                collectedLore.addLore(key);

                                PacketSyncLore.updateLore((EntityPlayerMP) event.player);

                                // Pickup notification packet
                                PacketHandler.INSTANCE.sendTo(new PacketClientNotification(key.page, key.dimension, PacketClientNotification.PICKUP), (EntityPlayerMP) event.player);

                                // Autoplay handling
                                LoreProperties properties = PlayerHandler.getCollectedLore(event.player);

                                if (lore.autoplay && properties.canAutoplay(key)) {
                                    properties.setAutoplayed(key, true);
                                    PacketHandler.INSTANCE.sendTo(new PacketClientNotification(key.page, key.dimension, PacketClientNotification.AUTOPLAY), (EntityPlayerMP) event.player);
                                }

                                for (Commands.CommandEntry command : lore.commands.commands) {
                                    if (command.delay > 0) {
                                        CommandDelayHandler.queueCommand(event.player, command);
                                    } else {
                                        CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
                                        LoreCommandSender commandSender = new LoreCommandSender(event.player);
                                        ch.executeCommand(commandSender, command.command);
                                    }
                                }
                            }

                            event.player.inventory.setInventorySlotContents(i, null);
                            event.player.inventory.markDirty();

                            return;
                        }

                        // Only set this to false if no lore has been updated this tick
                        notifiedThisTick = false;
                    }
                }
            }
        }
    }
}
