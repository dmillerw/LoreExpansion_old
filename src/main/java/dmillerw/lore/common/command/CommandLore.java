package dmillerw.lore.common.command;

import com.google.common.collect.Maps;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.item.ItemLorePage;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.LoreKey;
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
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                LoreLoader.clear();
                LoreLoader.initialize();
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Reloaded Lore"));
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (sender instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) sender;

                    long time = System.nanoTime();
                    if ((time - safeGet(player.getCommandSenderName())) > 5000000000L) {
                        player.addChatComponentMessage(new ChatComponentText("WARNING: This will erase ALL your collected lore. Run the command again to confirm"));
                        confirmationMap.put(player.getCommandSenderName(), time);
                    } else {
                        PlayerHandler.getCollectedLore(player).clear();
                        PacketSyncLore.updateLore((EntityPlayerMP) player);
                        player.addChatComponentMessage(new ChatComponentText("Cleared Lore"));
                        confirmationMap.remove(player.getCommandSenderName());
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equals("give")) {
                if (!validCategory(args[1])) {
                    sender.addChatMessage(new ChatComponentText(args[1] + " is not a valid Lore category!"));
                    return;
                }

                if (!validLore(args[1], args[2])) {
                    sender.addChatMessage(new ChatComponentText("Could not find lore '" + args[2] + "' in category '" + args[1] + "'"));
                    return;
                }

                giveLore((EntityPlayer) sender, args[1], args[2]);
            }
        } else if (args.length == 4) {
            if (!validCategory(args[2])) {
                sender.addChatMessage(new ChatComponentText(args[2] + " is not a valid Lore category!"));
                return;
            }

            if (!validLore(args[2], args[3])) {
                sender.addChatMessage(new ChatComponentText("Could not find lore '" + args[3] + "' in category '" + args[2] + "'"));
                return;
            }

            giveLore(getPlayer(sender, args[1]), args[2], args[3]);
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
