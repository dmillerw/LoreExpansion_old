package dmillerw.lore.common.lore.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public class LoreKey {

    public static LoreKey fromNBT(NBTTagCompound nbt) {
        int page = nbt.getInteger("page");
        int dimension = nbt.getInteger("dimension");
        return new LoreKey(page, dimension);
    }

    public static LoreKey fromLore(Lore lore) {
        return new LoreKey(lore.page, lore.dimension);
    }

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
        this.dimension = dimension;
        ;
    }

    public boolean global() {
        return dimension == Integer.MAX_VALUE;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("page", page);
        nbt.setInteger("dimension", dimension);
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

    public LoreKey copy() {
        return new LoreKey(this.page, this.dimension);
    }
}
