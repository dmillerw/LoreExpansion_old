package dmillerw.lore.lore;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.misc.StringHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author dmillerw
 */
public class PlayerHandler {

	private static Map<String, List<LoreKey>> playerLore = new HashMap<String, List<LoreKey>>();
	private static Map<String, List<LoreKey>> playerAutoplayLock = new HashMap<String, List<LoreKey>>();

	public static void clearLore(EntityPlayer player) {
		playerLore.remove(player.getCommandSenderName());
		playerAutoplayLock.remove(player.getCommandSenderName());
	}

	public static List<LoreKey> getLore(EntityPlayer player) {
		if (!playerLore.containsKey(player.getCommandSenderName())) {
			playerLore.put(player.getCommandSenderName(), new ArrayList<LoreKey>());
		}
		return playerLore.get(player.getCommandSenderName());
	}

	public static void setLore(EntityPlayer player, List<LoreKey> lore) {
		playerLore.put(player.getCommandSenderName(), lore);
		verifyLore(player);
		savePlayerLore(player);
	}

	public static void verifyLore(EntityPlayer player) {
		Iterator<LoreKey> iterator = playerLore.get(player.getCommandSenderName()).iterator();
		while (iterator.hasNext()) {
			LoreKey key = iterator.next();

			if (LoreLoader.INSTANCE.getLore(key) == null) {
				iterator.remove();
			}
		}
	}

	public static List<LoreKey> getLoreLock(EntityPlayer player) {
		if (!playerAutoplayLock.containsKey(player.getCommandSenderName())) {
			playerAutoplayLock.put(player.getCommandSenderName(), new ArrayList<LoreKey>());
		}
		return playerAutoplayLock.get(player.getCommandSenderName());
	}

	public static void setLoreLock(EntityPlayer player, List<LoreKey> lore) {
		playerAutoplayLock.put(player.getCommandSenderName(), lore);
		verifyLoreLock(player);
		savePlayerLore(player);
	}

	public static void verifyLoreLock(EntityPlayer player) {
		Iterator<LoreKey> iterator = playerAutoplayLock.get(player.getCommandSenderName()).iterator();
		while (iterator.hasNext()) {
			LoreKey key = iterator.next();

			if (LoreLoader.INSTANCE.getLore(key) == null) {
				iterator.remove();
			}
		}
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

	public static File getLockFileForPlayer(EntityPlayer player) {
		try {
			File directory = new File(player.worldObj.getSaveHandler().getWorldDirectory(), "players");
			return new File(directory, player.getCommandSenderName() + ".lorelock");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void loadPlayerLore(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				List<LoreKey> loaded = new ArrayList<LoreKey>();
				File file = getFileForPlayer(player);

				if (file != null && file.exists()) {
					FileInputStream inputStream = new FileInputStream(file);
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder sb = new StringBuilder();
					String line;

					while((line = reader.readLine()) != null) {
						if (!line.isEmpty()) {
							sb.append(line);
						}
					}

					reader.close();

					for (String str : sb.toString().split(";")) {
						if (!str.isEmpty()) {
							String[] split = str.split(":");
							int page = Integer.parseInt(split[0]);
							int dimension = 0;

							if (StringHelper.isInteger(split[1])) {
								dimension = Integer.parseInt(split[1]);
							} else {
								if (split[1].length() == 1 && split[1].equals("*")) {
									dimension = Integer.MAX_VALUE;
								}
							}

							loaded.add(new LoreKey(page, dimension));
						}
					}

					setLore(player, loaded);
				}
			} catch (Exception ex) {
				LoreExpansion.logger.fatal(String.format("Failed to load lore data for %s", player.getCommandSenderName()));
				ex.printStackTrace();
			}

			try {
				List<LoreKey> loaded = new ArrayList<LoreKey>();
				File file = getLockFileForPlayer(player);

				if (file != null && file.exists()) {
					FileInputStream inputStream = new FileInputStream(file);
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder sb = new StringBuilder();
					String line;

					while((line = reader.readLine()) != null) {
						if (!line.isEmpty()) {
							sb.append(line);
						}
					}

					reader.close();

					for (String str : sb.toString().split(";")) {
						if (!str.isEmpty()) {
							String[] split = str.split(":");
							int page = Integer.parseInt(split[0]);
							int dimension = 0;

							if (StringHelper.isInteger(split[1])) {
								dimension = Integer.parseInt(split[1]);
							} else {
								if (split[1].length() == 1 && split[1].equals("*")) {
									dimension = Integer.MAX_VALUE;
								}
							}

							loaded.add(new LoreKey(page, dimension));
						}
					}

					setLoreLock(player, loaded);
				}
			} catch (Exception ex) {
				LoreExpansion.logger.fatal(String.format("Failed to load autoplay data for %s", player.getCommandSenderName()));
				ex.printStackTrace();
			}
		}
	}

	public static void savePlayerLore(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				File file = getFileForPlayer(player);
				List<LoreKey> list = getLore(player);

				StringBuilder sb = new StringBuilder();
				for (int i=0; i<list.size(); i++) {
					LoreKey key = list.get(i);
					sb.append(String.valueOf(key.page));
					sb.append(":");
					if (key.dimension != Integer.MAX_VALUE) {
						sb.append(String.valueOf(key.dimension));
					} else {
						sb.append("*");
					}
					sb.append(";");
				}

				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(sb.toString().getBytes(Charset.forName("UTF-8")));
				outputStream.close();
			} catch (Exception exception1) {
				LoreExpansion.logger.fatal(String.format("Failed to save lore data for %s", player.getCommandSenderName()));
				exception1.printStackTrace();
			}

			try {
				File file = getLockFileForPlayer(player);
				List<LoreKey> list = getLoreLock(player);

				StringBuilder sb = new StringBuilder();
				for (int i=0; i<list.size(); i++) {
					LoreKey key = list.get(i);
					sb.append(String.valueOf(key.page));
					sb.append(":");
					if (key.dimension != Integer.MAX_VALUE) {
						sb.append(String.valueOf(key.dimension));
					} else {
						sb.append("*");
					}
					sb.append(";");
				}

				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(sb.toString().getBytes(Charset.forName("UTF-8")));
				outputStream.close();
			} catch (Exception exception1) {
				LoreExpansion.logger.fatal(String.format("Failed to save lore data for %s", player.getCommandSenderName()));
				exception1.printStackTrace();
			}
		}
	}
}
