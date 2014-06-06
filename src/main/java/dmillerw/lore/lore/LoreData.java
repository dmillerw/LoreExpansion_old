package dmillerw.lore.lore;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Hash code is based off page, so there can't be conflicts
 * @author dmillerw
 */
public class LoreData {

	public static class DeserializedLoreTag {
		public String defaultTag = "Lore Pages:";
		public Map<Integer, String> mapping = Maps.newHashMap();
	}

	public static class DeserializedLore {
		public int page;
		public int dimension = 0; // Default
		public String title;
		public String lore;
		public String sound;
	}

	public int page;
	public Set<Integer> contents = Sets.newHashSet();
	public Map<Integer, String> title = Maps.newHashMap();
	public Map<Integer, String> lore = Maps.newHashMap();
	public Map<Integer, String> sound = Maps.newHashMap();

	public LoreData addLore(DeserializedLore data) {
		if (page != data.page) {
			return this;
		}

		if (contents.contains(data.dimension)) {
			// ERROR
			return this;
		}

		contents.add(data.dimension);
		title.put(data.dimension, data.title);
		lore.put(data.dimension, data.lore);
		sound.put(data.dimension, data.sound);

		return this;
	}

	public boolean validForDimension(int dimension) {
		return contents.contains(dimension);
	}

	public String getTitle(int dimension) {
		return title.get(dimension);
	}

	public String getLore(int dimension) {
		return lore.get(dimension);
	}

	public String getSound(int dimension) {
		return sound.get(dimension);
	}

	public boolean hasTitle(int dimension) {
		return title.containsKey(dimension);
	}

	public boolean hasLore(int dimension) {
		return lore.containsKey(dimension);
	}

	public boolean hasSound(int dimension) {
		return sound.containsKey(dimension);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoreData data = (LoreData) o;

		if (page != data.page) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return page;
	}
}
