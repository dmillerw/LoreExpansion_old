package dmillerw.lore.lore;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.lore.data.LoreTags;
import dmillerw.lore.lore.json.CommandDeserializer;
import dmillerw.lore.lore.json.LoreDeserializer;
import dmillerw.lore.lore.json.TagDeserializer;
import dmillerw.lore.lore.json.TagSerializer;
import dmillerw.lore.misc.FileHelper;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class LoreLoader {

	public static void initialize() {
		for (File file : LoreExpansion.loreFolder.listFiles()) {
			if (FileHelper.isJSONFile(file)) {
				try {
					LoreLoader.INSTANCE.loadLore(file);
				} catch (Exception ex) {
					LoreExpansion.logger.warn(String.format("Failed to parse %s", file.getName()));
					ex.printStackTrace();
				}
			}
		}

		File tagFile = new File(LoreExpansion.configFolder + "/tags.json");
		if (tagFile.exists()) {
			LoreLoader.INSTANCE.loadLoreTags(tagFile);
		} else {
			try {
				LoreLoader.INSTANCE.saveDefaultLoreTags(tagFile);
			} catch (IOException ex) {
				LoreExpansion.logger.warn(String.format("Failed to save default tags.json. This isn't a huge issue."));
			}
		}
	}

	public static final LoreLoader INSTANCE = new LoreLoader();

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Lore.class, new LoreDeserializer());
		builder.registerTypeAdapter(LoreTags.class, new TagDeserializer());
		builder.registerTypeAdapter(LoreTags.class, new TagSerializer());
		builder.registerTypeAdapter(Lore.CommandWrapper.class, new CommandDeserializer());
		gson = builder.create();
	}

	private Map<LoreKey, Lore> lore = Maps.newHashMap();

	private LoreTags loreTags = new LoreTags();

	public int[] getAllDimensions() {
		List<Integer> list = new ArrayList<Integer>();
		for (Lore lore : getAllLore()) {
			if (!list.contains(lore.dimension)) {
				list.add(lore.dimension);
			}
		}
		return ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()]));
	}

	public Lore[] getAllLore() {
		return lore.values().toArray(new Lore[lore.size()]);
	}

	private String getDimensionName(int dimension) {
		return dimension == Integer.MAX_VALUE ? "Global" : DimensionManager.getProvider(dimension).getDimensionName();
	}

	public String getLoreTag(int dimension) {
		String tag = loreTags.defaultTag;
		if (loreTags.mapping.containsKey(dimension)) {
			tag = loreTags.mapping.get(dimension);
		}
		return String.format(tag, getDimensionName(dimension));
	}

	public Lore getLore(int page, int dimension) {
		LoreKey key = new LoreKey(page, dimension);
		return getLore(key);
	}

	public Lore getLore(LoreKey key) {
		if (!lore.containsKey(key)) {
			return null;
		}
		return lore.get(key);
	}

	public void clear() {
		lore.clear();
		loreTags = new LoreTags();
	}

	public void loadLore(File file) throws Exception {
		Lore data = gson.fromJson(new FileReader(file), Lore.class);
		// Make sure page is positive and above 0
		if (data.page <= 0) {
			LoreExpansion.logger.warn(String.format("Page number in %s must be above 0. Setting to 1", file.getName()));
			data.page = 1;
		}
		// Check to see if file exists
		if (!data.sound.isEmpty()) {
			File audio = new File(LoreExpansion.audioFolder, data.sound + ".ogg");
			if (!audio.exists() || !audio.isFile()) {
				LoreExpansion.logger.warn(String.format("Could not find %s audio file as defined in %s", data.sound, file.getName()));
				data.sound = "";
			}
		}

		LoreKey key = new LoreKey(data.page, data.dimension);
		lore.put(key, data);
	}

	public void loadLoreTags(File file) {
		try {
			loreTags = gson.fromJson(new FileReader(file), LoreTags.class);
		} catch (IOException ex) {
			// LOG ERROR
		}
	}

	public void saveDefaultLoreTags(File file) throws IOException {
		LoreTags defaultTags = new LoreTags();
		String json = gson.toJson(defaultTags, LoreTags.class);
		FileWriter writer = new FileWriter(file);
		writer.append(json);
		writer.close();
	}
}
