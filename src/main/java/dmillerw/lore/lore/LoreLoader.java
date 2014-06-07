package dmillerw.lore.lore;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.misc.FileHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	private static final int MAX = 256;

	public static final LoreLoader INSTANCE = new LoreLoader();

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(LoreData.DeserializedLore.class, new LoreDeserializer());
		builder.registerTypeAdapter(LoreData.DeserializedLoreTag.class, new TagDeserializer());
		builder.registerTypeAdapter(LoreData.DeserializedLoreTag.class, new TagSerializer());
		gson = builder.create();
	}

	private Map<Integer, LoreData> lore = Maps.newHashMap();

	private LoreData.DeserializedLoreTag loreTags = new LoreData.DeserializedLoreTag();

	public LoreData[] getLore() {
		return lore.values().toArray(new LoreData[lore.size()]);
	}

	public String getTag(int dimension) {
		return loreTags.mapping.containsKey(dimension) ? loreTags.mapping.get(dimension) : loreTags.defaultTag;
	}

	public LoreData getLore(int page) {
		if (!lore.containsKey(page)) {
			LoreData data = new LoreData();
			data.page = page;
			lore.put(page, data);
		}
		return lore.get(page);
	}

	public void clear() {
		lore.clear();
		loreTags = new LoreData.DeserializedLoreTag();
	}

	public void loadLore(File file) throws Exception {
		LoreData.DeserializedLore data = gson.fromJson(new FileReader(file), LoreData.DeserializedLore.class);
		// Make sure page is positive and above 0
		if (data.page <= 0) {
			LoreExpansion.logger.warn(String.format("Page number in %s must be above 0. Setting to 1", file.getName()));
			data.page = 1;
		}
		// Check to see if file exists
		if (!data.sound.isEmpty()) {
			File audio = new File(LoreExpansion.audioFolder, data.sound);
			if (!audio.exists() || !audio.isFile()) {
				LoreExpansion.logger.warn(String.format("Could not find %s audio file as defined in %s", data.sound, file.getName()));
				data.sound = "";
			}
		}
		LoreData lore = getLore(data.page);
		lore.addLore(data);
	}

	public void loadLoreTags(File file) {
		try {
			loreTags = gson.fromJson(new FileReader(file), LoreData.DeserializedLoreTag.class);
		} catch (IOException ex) {
			// LOG ERROR
		}
	}

	public void saveDefaultLoreTags(File file) throws IOException {
		LoreData.DeserializedLoreTag defaultTags = new LoreData.DeserializedLoreTag();
		String json = gson.toJson(defaultTags, LoreData.DeserializedLoreTag.class);
		FileWriter writer = new FileWriter(file);
		writer.append(json);
		writer.close();
	}
}
