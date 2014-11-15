package dmillerw.lore.common.command;

import com.google.common.collect.Maps;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.network.packet.PacketSyncLore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        if (args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        if (args[0].equalsIgnoreCase("reload")) {
            LoreLoader.INSTANCE.clear();
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
    }

    private long safeGet(String key) {
        return confirmationMap.containsKey(key) ? confirmationMap.get(key) : 0L;
    }
}
