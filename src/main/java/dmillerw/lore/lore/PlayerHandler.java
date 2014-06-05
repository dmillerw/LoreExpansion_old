package dmillerw.lore.lore;

import dmillerw.lore.LoreExpansion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author dmillerw
 */
public class PlayerHandler {

	private static Map<String, List<Integer>> playerLore = new HashMap<String, List<Integer>>();

	public static void clearLore(EntityPlayer player) {
		playerLore.remove(player.getCommandSenderName());
	}

	public static List<Integer> getLore(EntityPlayer player) {
		if (!playerLore.containsKey(player.getCommandSenderName())) {
			playerLore.put(player.getCommandSenderName(), new ArrayList<Integer>());
		}
		return playerLore.get(player.getCommandSenderName());
	}

	public static void setLore(EntityPlayer player, List<Integer> lore) {
		playerLore.put(player.getCommandSenderName(), lore);
	}

	public static File getFileForPlayer(EntityPlayer player) {
		try {
			File directory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			return new File(directory, player.getCommandSenderName() + ".lore");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static File getBackupFileForPlayer(EntityPlayer player) {
		try {
			File directory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			return new File(directory, player.getCommandSenderName() + ".loreback");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void loadPlayerLore(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				NBTTagCompound data = null;
				boolean save = false;
				File file = getFileForPlayer(player);

				if (file != null && file.exists()) {
					try {
						FileInputStream fileInputStream = new FileInputStream(file);
						data = CompressedStreamTools.readCompressed(fileInputStream);
						fileInputStream.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				if (file == null || !file.exists() || data == null || data.hasNoTags()) {
					LoreExpansion.logger.warn("Lore data not found for %s. Attempting to load backup file", player.getCommandSenderName());
					file = getBackupFileForPlayer(player);
					if (file != null && file.exists()) {
						try {
							FileInputStream fileinputstream = new FileInputStream(file);
							data = CompressedStreamTools.readCompressed(fileinputstream);
							fileinputstream.close();
							save = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				if (data != null) {
					int[] array = data.getIntArray("lore");
					List<Integer> list = new ArrayList<Integer>();
					for (int i=0; i<array.length; i++) {
						list.add(array[i]);
					}
					setLore(player, list);
				}
			} catch (Exception ex) {
				LoreExpansion.logger.fatal("Failed to load lore data for %s", player.getCommandSenderName());
				ex.printStackTrace();
			}
		}
	}

	public static void savePlayerBaubles(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				File file2 = getBackupFileForPlayer(player);
				if (file2 != null && file2.exists()) {
					try {
						file2.delete();
					} catch (Exception e) {
						LoreExpansion.logger.error("Could not delete backup file for player " + player.getCommandSenderName());
					}
				}

				File file1 = getFileForPlayer(player);
				file2 = getBackupFileForPlayer(player);
				if (file1 != null && file1.exists()) {
					try {
						file1.renameTo(file2);
					} catch (Exception e) {
						LoreExpansion.logger.error("Could not backup old baubles file for player " + player.getCommandSenderName());
					}
				}

				file1 = getFileForPlayer(player);
				try {
					if (file1 != null) {
						List<Integer> list = getLore(player);
						NBTTagCompound data = new NBTTagCompound();
						NBTTagIntArray array = new NBTTagIntArray(ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()])));
						data.setTag("lore", array);

						FileOutputStream fileoutputstream = new FileOutputStream(file1);
						CompressedStreamTools.writeCompressed(data, fileoutputstream);
						fileoutputstream.close();

					}
				} catch (Exception e) {
					LoreExpansion.logger.error("Could not save baubles file for player " + player.getCommandSenderName());
					e.printStackTrace();
					if (file1.exists()) {
						try {
							file1.delete();
						} catch (Exception e2) {
						}
					}
				}
			} catch (Exception exception1) {
				LoreExpansion.logger.fatal("Error saving baubles inventory");
				exception1.printStackTrace();
			}
		}
	}
}
