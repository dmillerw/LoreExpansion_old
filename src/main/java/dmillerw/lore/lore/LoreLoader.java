package dmillerw.lore.lore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author dmillerw
 */
public class LoreLoader {

	private static final int MAX = 256;

	public static final LoreLoader INSTANCE = new LoreLoader();

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(LoreData.class, new LoreDeserializer());
		builder.registerTypeAdapter(LoreData.DeserializedLoreTag.class, new TagDeserializer());
		builder.registerTypeAdapter(LoreData.DeserializedLoreTag.class, new TagSerializer());
		gson = builder.create();
	}

	private LoreData[] lore = new LoreData[MAX];

	private LoreData.DeserializedLoreTag loreTags = new LoreData.DeserializedLoreTag();

	public LoreData[] getLore() {
		return lore;
	}

	public String getTag(int dimension) {
		return loreTags.mapping.containsKey(dimension) ? loreTags.mapping.get(dimension) : loreTags.defaultTag;
	}

	public LoreData getLore(int page) {
		if (lore[page] == null) {
			lore[page] = new LoreData();
			lore[page].page = page;
		}
		return lore[page];
	}

	public void loadLore(File file) throws Exception {
		LoreData.DeserializedLore data = gson.fromJson(new FileReader(file), LoreData.DeserializedLore.class);
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
