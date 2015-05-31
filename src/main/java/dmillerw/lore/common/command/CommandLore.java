package dmillerw.lore.common.command;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.item.ItemLorePage;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.entity.LoreProperties;
import dmillerw.lore.common.network.packet.PacketHandler;
import dmillerw.lore.common.network.packet.PacketReloadLore;
import dmillerw.lore.common.network.packet.PacketSyncLore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class CommandLore extends CommandBase {

    private static Map<String, Long> confirmationMap = Maps.newHashMap();

    @Override
    public String getCommandName() {
        return "lore";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lore <reload/clear>";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] array) {
        if (array.length != 0 && (array[0].equalsIgnoreCase("clear") || array[0].equalsIgnoreCase("reload"))) {
            return super.addTabCompletionOptions(sender, array);
        } else {
            return Arrays.asList("reload", "clear");
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // Reload
        if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
            LoreLoader.clear();
            LoreLoader.initialize();

            if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
                PacketHandler.INSTANCE.sendToAll(new PacketReloadLore());
            }

            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Reloaded Lore"));

            return;
        }

        // Clear
        if (args[0].equalsIgnoreCase("clear")) {
            // All, self
            if (args.length == 2) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;
                    PlayerHandler.getCollectedLore(player).clear();
                    PacketSyncLore.updateLore((EntityPlayerMP) player);
                    player.addChatComponentMessage(new ChatComponentText("Cleared your Lore"));
                }
            // Either specific self, or all on another player
            } else if (args.length == 3) {
                if (args[2].equals("all")) {
                    EntityPlayer player = getPlayer(sender, args[1]);
                    if (player == null) {
                        sender.addChatMessage(new ChatComponentText("Could not find player" + args[1]));
                        return;
                    }

                    PlayerHandler.getCollectedLore(player).clear();
                    PacketSyncLore.updateLore((EntityPlayerMP) player);
                    player.addChatComponentMessage(new ChatComponentText(String.format("Cleared %s's Lore", player.getDisplayName())));
                } else {
                    EntityPlayer player = (EntityPlayer) sender;
                    String category = args[1];
                    String ident = args[2];

                    PlayerHandler.getCollectedLore(player).removeLore(new LoreKey(category, ident));

                    player.addChatComponentMessage(new ChatComponentText(String.format("Removed Lore [%s, %s]", category, ident)));
                }
            // Specific, other player
            } else if (args.length == 4) {
                EntityPlayer player = getPlayer(sender, args[1]);
                if (player == null) {
                    sender.addChatMessage(new ChatComponentText("Could not find player" + args[1]));
                    return;
                }

                String category = args[2];
                String ident = args[3];

                PlayerHandler.getCollectedLore(player).removeLore(new LoreKey(category, ident));

                player.addChatComponentMessage(new ChatComponentText(String.format("Removed Lore [%s, %s] from %s", category, ident, player.getDisplayName())));
            }

            return;
        }

        // Give
        if (args[0].equals("give")) {
            // Self, all
            if (args.length == 2) {
                if (args[1].equals("all")) {
                    Set<Lore> lores = LoreLoader.getAllLore();
                    for (Lore lore : lores) {
                        final LoreKey key = LoreKey.fromLore(lore);

                        LoreProperties properties = PlayerHandler.getCollectedLore((EntityPlayer) sender);
                        if (!properties.hasLore(key))
                            properties.addLore(key);
                    }

                    PacketSyncLore.updateLore((EntityPlayerMP) sender);

                    sender.addChatMessage(new ChatComponentText("Gave " + ((EntityPlayer)sender).getDisplayName() + " all lore"));

                    return;
                }
            }

            if (args.length == 3) {
                // Other, all
                if (args[2].equals("all")) {
                    EntityPlayer player = getPlayer(sender, args[1]);
                    if (player == null) {
                        sender.addChatMessage(new ChatComponentText("Could not find player" + args[1]));
                        return;
                    }

                    Set<Lore> lores = LoreLoader.getAllLore();
                    for (Lore lore : lores) {
                        final LoreKey key = LoreKey.fromLore(lore);

                        LoreProperties properties = PlayerHandler.getCollectedLore(player);
                        if (!properties.hasLore(key))
                            properties.addLore(key);
                    }

                    PacketSyncLore.updateLore((EntityPlayerMP) sender);

                    sender.addChatMessage(new ChatComponentText("Gave Player "  + player.getDisplayName() + " all lore"));

                    return;
                // Self, specific
                } else {
                    if (!validCategory(args[1])) {
                        sender.addChatMessage(new ChatComponentText(args[1] + " is not a valid Lore category!"));
                        return;
                    }

                    if (!validLore(args[1], args[2])) {
                        sender.addChatMessage(new ChatComponentText("Could not find lore '" + args[2] + "' in category '" + args[1] + "'"));
                        return;
                    }

                    giveLore((EntityPlayer) sender, args[1], args[2]);

                    return;
                }
            }

            // Other, specific
            if (args.length == 4) {
                if (!validCategory(args[2])) {
                    sender.addChatMessage(new ChatComponentText(args[2] + " is not a valid Lore category!"));
                    return;
                }

                if (!validLore(args[2], args[3])) {
                    sender.addChatMessage(new ChatComponentText("Could not find lore '" + args[3] + "' in category '" + args[2] + "'"));
                    return;
                }

                EntityPlayer player = getPlayer(sender, args[1]);
                if (player == null) {
                    sender.addChatMessage(new ChatComponentText("Could not find player" + args[1]));
                    return;
                }

                giveLore(player, args[2], args[3]);

                return;
            }
        }
    }

    private boolean validCategory(String category) {
        return LoreLoader.getAllCategories().contains(category);
    }

    private boolean validLore(String category, String ident) {
        return LoreLoader.getLore(category, ident) != null;
    }

    private void giveLore(EntityPlayer entityPlayer, String category, String ident) {
        LoreKey key = new LoreKey(category, ident);
        ItemStack itemStack = new ItemStack(LoreExpansion.lorePage);
        ItemLorePage.setLore(itemStack, key);
        entityPlayer.inventory.addItemStackToInventory(itemStack);
    }

    private long safeGet(String key) {
        return confirmationMap.containsKey(key) ? confirmationMap.get(key) : 0L;
    }
}
