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
		public boolean global = true; // Default
		public int dimension = 0; // Default
		public String title;
		public String lore;
		public String sound;
	}

	public int page;

	public boolean global = false;

	public Set<Integer> contents = Sets.newHashSet();
	public Map<Integer, String> title = Maps.newHashMap();
	public Map<Integer, String> lore = Maps.newHashMap();
	public Map<Integer, String> sound = Maps.newHashMap();

	public LoreData addLore(DeserializedLore data) {
		if (page != data.page) {
			return this;
		}

		if (contents.contains(data.dimension) || global) {
			// ERROR
			return this;
		}

		if (!global && data.global) {
			global = true;
		}

		contents.add(data.global ? 0 : data.dimension);
		title.put(data.global ? 0 : data.dimension, data.title);
		lore.put(data.global ? 0 : data.dimension, data.lore);
		sound.put(data.global ? 0 : data.dimension, data.sound);

		return this;
	}

	public boolean validForDimension(int dimension) {
		return contents.contains(dimension) || global;
	}

	public String getTitle(int dimension) {
		return global ? title.get(0) : title.get(dimension);
	}

	public String getLore(int dimension) {
		return global ? lore.get(0) : lore.get(dimension);
	}

	public String getSound(int dimension) {
		return global ? sound.get(0) : sound.get(dimension);
	}

	public boolean hasTitle(int dimension) {
		return global || title.containsKey(dimension);
	}

	public boolean hasLore(int dimension) {
		return global || lore.containsKey(dimension);
	}

	public boolean hasSound(int dimension) {
		return global || sound.containsKey(dimension);
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
