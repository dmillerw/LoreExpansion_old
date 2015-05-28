package dmillerw.lore.common.item;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.core.TabLore;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.network.packet.PacketSyncLore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemLorePage extends Item {

    public static void setLore(ItemStack stack, LoreKey key) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        nbt.setInteger("page", key.page);
        nbt.setInteger("dimension", key.dimension);
        stack.setTagCompound(nbt);
    }

    public static LoreKey getLore(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        int page = nbt.getInteger("page");
        int dimension = nbt.getInteger("dimension");
        return new LoreKey(page, dimension);
    }

    private IIcon icon;

    public ItemLorePage() {
        super();

        setMaxDamage(0);
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(TabLore.TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.capabilities.isCreativeMode) {
                LoreKey key = ItemLorePage.getLore(stack);
                if (key != null) {
                    Lore data = LoreLoader.INSTANCE.getLore(key);

                    if (data == null) {
                        LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
                        stack.setTagCompound(new NBTTagCompound());
                    }

                    PlayerHandler.getCollectedLore(player).addLore(key);

                    PacketSyncLore.updateLore((EntityPlayerMP) player);

                    player.addChatComponentMessage(new ChatComponentText("Added lore page #" + data.page));
                }
            }
        }

        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
        LoreKey key = ItemLorePage.getLore(stack);
        if (key != null) {
            Lore data = LoreLoader.INSTANCE.getLore(key);

            if (data == null) {
                LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
                stack.setTagCompound(new NBTTagCompound());
            }

            if (data != null) {
                list.add(String.format("Page %s: %s", key.page, data.title));
                if (key.dimension == Integer.MAX_VALUE) {
                    list.add("Global");
                } else {
                    list.add("Dimension: " + DimensionManager.getProvider(data.dimension).getDimensionName());
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        LoreKey key = ItemLorePage.getLore(stack);
        if (key != null) {
            Lore data = LoreLoader.INSTANCE.getLore(key);

            if (data != null) {
                return "Lore: " + data.title;
            } else {
                return super.getItemStackDisplayName(stack);
            }
        } else {
            return super.getItemStackDisplayName(stack);
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (Lore data : LoreLoader.INSTANCE.getAllLore()) {
            if (data != null) {
                ItemStack stack = new ItemStack(this);
                ItemLorePage.setLore(stack, new LoreKey(data));
                list.add(stack);
            }
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon = register.registerIcon("loreexp:scrap");
    }
}
