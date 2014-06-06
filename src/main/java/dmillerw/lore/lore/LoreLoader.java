package dmillerw.lore.lore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;

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
		gson = builder.create();
	}

	private LoreData[] lore = new LoreData[MAX];

	public LoreData[] getLore() {
		return lore;
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
}
