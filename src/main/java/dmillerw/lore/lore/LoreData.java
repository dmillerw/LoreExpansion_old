package dmillerw.lore.lore;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dmillerw.lore.client.sound.SoundLoader;

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

		@Override
		public String toString() {
			return "page: " + page + " global: " + global + " dimension: " + dimension + " title: " + title + " lore: " + lore + " sound: " + sound;
		}
	}

	public int page;

	public boolean global = false;
	public String globalTitle;
	public String globalLore;
	public SoundLoader globalSound;

	public Set<Integer> contents = Sets.newHashSet();
	public Map<Integer, String> title = Maps.newHashMap();
	public Map<Integer, String> lore = Maps.newHashMap();
	public Map<Integer, SoundLoader> sound = Maps.newHashMap();

	public boolean addLore(DeserializedLore data) {
		// Obviously different pages can't be merged
		if (page != data.page) {
			return false;
		}

		// Don't allow a non-global page to become global
		if (data.global && !contents.isEmpty()) {
			return false;
		}

		// If it's already set to global, or the page already has data for this dimension, deny
		if (global || contents.contains(data.dimension)) {
			// ERROR
			return false;
		}

		// If not already global, and should be, set
		if (!global && data.global) {
			global = true;
		}

		if (!global) {
			contents.add(data.dimension);
			title.put(data.dimension, data.title);
			lore.put(data.dimension, data.lore);
			sound.put(data.dimension, new SoundLoader(data.sound));
		} else {
			globalTitle = data.title;
			globalLore = data.lore;
			globalSound = new SoundLoader(data.sound);
		}

		return true;
	}

	public boolean validForDimension(int dimension) {
		return global || contents.contains(dimension);
	}

	public String getTitle(int dimension) {
		return global ? globalTitle : title.get(dimension);
	}

	public String getLore(int dimension) {
		return global ? globalLore : lore.get(dimension);
	}

	public SoundLoader getSound(int dimension) {
		return global ? globalSound : sound.get(dimension);
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

	public void preloadSounds() {
		if (globalSound != null) {
			globalSound.registerSound();
			globalSound.start();
			globalSound.stop();
		} else {
			for (Map.Entry<Integer, SoundLoader> entry : sound.entrySet()) {
				entry.getValue().registerSound();
				entry.getValue().start();
				entry.getValue().stop();
			}
		}
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
