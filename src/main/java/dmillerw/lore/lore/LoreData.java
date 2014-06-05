package dmillerw.lore.lore;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dmillerw.lore.LoreExpansion;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.Set;

/**
 * Hash code is based off page, so there can't be conflicts
 * @author dmillerw
 */
public class LoreData {

	public static class DimensionString {
		public int dimension;
		public String string;

		public DimensionString() {

		}

		public DimensionString(int dimension, String string) {
			this.dimension = dimension;
			this.string = string;
		}

		public DimensionString readFromNBT(NBTTagCompound nbt) {
			dimension = nbt.getInteger("dimension");
			string = nbt.getString("string");
			return this;
		}

		public DimensionString writeToNBT(NBTTagCompound nbt) {
			nbt.setInteger("dimension", dimension);
			nbt.setString("string", string);
			return this;
		}
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
