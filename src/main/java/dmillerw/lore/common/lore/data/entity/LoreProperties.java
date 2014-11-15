package dmillerw.lore.common.lore.data.entity;

import dmillerw.lore.common.lore.data.LoreKey;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class LoreProperties implements IExtendedEntityProperties {

    private List<LoreKey> collectedLore = new ArrayList<LoreKey>();
    private List<LoreKey> autoplayed = new ArrayList<LoreKey>();

    @Override
    public void init(Entity entity, World world) {

    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            LoreKey key = LoreKey.fromNBT(tag);
            collectedLore.add(key);
            if (tag.hasKey("played")) {
                autoplayed.add(key);
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (LoreKey key : collectedLore) {
            NBTTagCompound tag = new NBTTagCompound();
            key.writeToNBT(tag);
            if (autoplayed.contains(tag)) {
                tag.setBoolean("played", true);
            }
            list.appendTag(tag);
        }
        compound.setTag("list", list);
    }

    public List<LoreKey> getLore() {
        return collectedLore;
    }

    public void clear() {
        collectedLore.clear();
        autoplayed.clear();
    }

    public void addLore(LoreKey key) {
        collectedLore.add(key);
    }

    public boolean canAutoplay(LoreKey key) {
        return !autoplayed.contains(key);
    }

    public void setAutoplayed(LoreKey key, boolean state) {
        if (state && !autoplayed.contains(key)) {
            autoplayed.add(key);
        } else if (!state && autoplayed.contains(key)) {
            autoplayed.remove(key);
        }
    }
}
