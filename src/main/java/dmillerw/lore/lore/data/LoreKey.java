package dmillerw.lore.lore.data;

/**
 * @author dmillerw
 */
public class LoreKey {

	public final int page;
	public final int dimension;

	public LoreKey(Lore lore) {
		this(lore.page, lore.dimension);
	}

	public LoreKey(int page) {
		this(page, Integer.MAX_VALUE);
	}

	public LoreKey(int page, int dimension) {
		this.page = page;
		this.dimension = dimension;;
	}

	public boolean global() {
		return dimension == Integer.MAX_VALUE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LoreKey loreKey = (LoreKey) o;

		if (dimension != loreKey.dimension) return false;
		if (page != loreKey.page) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = page;
		result = 31 * result + dimension;
		return result;
	}
}
