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
        key.writeToNBT(nbt);
        stack.setTagCompound(nbt);
    }

    public static LoreKey getLore(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }

        return LoreKey.fromNBT(stack.getTagCompound());
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
                    Lore data = LoreLoader.getLore(key);

                    if (data == null) {
                        LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
                        stack.setTagCompound(new NBTTagCompound());
                    }

                    PlayerHandler.getCollectedLore(player).addLore(key);
                    PacketSyncLore.updateLore((EntityPlayerMP) player);

                    player.addChatComponentMessage(new ChatComponentText(String.format("Added lore page '%s'", key.ident)));
                }
            }
        }

        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
        LoreKey key = ItemLorePage.getLore(stack);
        if (key != null) {
            Lore data = LoreLoader.getLore(key);

            if (data == null) {
                LoreExpansion.logger.warn("Found item with invalid lore. Resetting");
                stack.setTagCompound(new NBTTagCompound());
            }

            if (data != null) {
                list.add(String.format("Title: %s", data.title));
                list.add(String.format("Category: %s", data.category));
            }
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (Lore data : LoreLoader.getAllLore()) {
            if (data != null) {
                ItemStack stack = new ItemStack(this);
                ItemLorePage.setLore(stack, LoreKey.fromLore(data));
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
