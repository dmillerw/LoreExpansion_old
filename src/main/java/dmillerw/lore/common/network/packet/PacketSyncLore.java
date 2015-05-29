package dmillerw.lore.common.network.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.gui.GuiJournal;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.LoreKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class PacketSyncLore implements IMessage {

    public static void updateLore(EntityPlayerMP player) {
        PacketHandler.INSTANCE.sendTo(new PacketSyncLore(player), player);
    }

    private List<LoreKey> lore;

    public PacketSyncLore() {

    }

    public PacketSyncLore(EntityPlayer player) {
        this.lore = PlayerHandler.getCollectedLore(player).getLore();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(lore.size());
        if (!lore.isEmpty()) {
            for (int i = 0; i < lore.size(); i++) {
                LoreKey key = lore.get(i);

                ByteBufUtils.writeUTF8String(buf, key.category);
                ByteBufUtils.writeUTF8String(buf, key.ident);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        lore = new ArrayList<LoreKey>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            lore.add(new LoreKey(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf)));
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncLore, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncLore message, MessageContext ctx) {
            World world = LoreExpansion.proxy.getClientWorld();
            if (world == null) {
                return null;
            }

            GuiJournal.playerLore.clear();
            GuiJournal.playerLore = message.lore;

            if (!GuiJournal.playerLore.contains(GuiJournal.selectedLore)) {
                GuiJournal.selectedLore = null;
                SoundHandler.INSTANCE.stop(); // Just in case
            }

            return null;
        }
    }
}
