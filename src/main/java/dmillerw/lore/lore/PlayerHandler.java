package dmillerw.lore.lore;

import dmillerw.lore.LoreExpansion;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		savePlayerLore(player);
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

	public static void loadPlayerLore(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				List<Integer> loaded = new ArrayList<Integer>();
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
							loaded.add(Integer.parseInt(str));
						}
					}

					setLore(player, loaded);
				}
			} catch (Exception ex) {
				LoreExpansion.logger.fatal(String.format("Failed to load lore data for %s", player.getCommandSenderName()));
				ex.printStackTrace();
			}
		}
	}

	public static void savePlayerLore(EntityPlayer player) {
		if (player != null && !player.worldObj.isRemote) {
			try {
				File file = getFileForPlayer(player);
				List<Integer> list = getLore(player);

				StringBuilder sb = new StringBuilder();
				for (int i=0; i<list.size(); i++) {
					sb.append(String.valueOf(list.get(i)));
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
