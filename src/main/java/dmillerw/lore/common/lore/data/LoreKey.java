package dmillerw.lore.common.lore.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public class LoreKey {

    public static LoreKey fromNBT(NBTTagCompound nbt) {
        if (nbt == null)
            return null;

        return new LoreKey(nbt.getString("category"), nbt.getString("ident"));
    }

    public static LoreKey fromLore(Lore lore) {
        if (lore == null)
            return null;

        return new LoreKey(lore.category, lore.ident);
    }

    public final String category;
    public final String ident;

    public LoreKey(String category, String ident) {
        this.category = category;
        this.ident = ident;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("category", category);
        nbt.setString("ident", ident);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoreKey loreKey = (LoreKey) o;

        if (!category.equals(loreKey.category)) return false;
        return ident.equals(loreKey.ident);

    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + ident.hashCode();
        return result;
    }

    public LoreKey copy() {
        return new LoreKey(category, ident);
    }
}
