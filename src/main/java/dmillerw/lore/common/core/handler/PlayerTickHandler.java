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
import dmillerw.lore.common.network.packet.PacketNotification;
import dmillerw.lore.common.network.packet.PacketSyncLore;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
                            Lore lore = LoreLoader.getLore(key);

                            if (lore == null) {
                                return;
                            }

                            LoreProperties collectedLore = PlayerHandler.getCollectedLore(event.player);
                            if (!collectedLore.hasLore(key)) {
                                collectedLore.addLore(key);

                                PacketSyncLore.updateLore((EntityPlayerMP) event.player);


                                // Pickup notification packet
                                PacketNotification.notify(event.player, PacketNotification.TYPE_CLIENT_PICKUP, key);

                                // Autoplay handling
                                LoreProperties properties = PlayerHandler.getCollectedLore(event.player);
                                if (lore.autoplay && properties.canAutoplay(key)) {
                                    properties.setAutoplayed(key, true);
                                    PacketNotification.notify(event.player, PacketNotification.TYPE_CLIENT_AUTOPLAY, key);
                                }

                                if (lore.commands.commands != null) {
                                    for (Commands.CommandEntry command : lore.commands.commands) {
                                        if (command.delay > 0) {
                                            CommandDelayHandler.queueCommand(event.player, command);
                                        } else {
                                            CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
                                            LoreCommandSender commandSender = new LoreCommandSender(event.player);
                                            for (String c : command.commands) {
                                                ch.executeCommand(commandSender, c);
                                            }
                                        }
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
