package dmillerw.lore.common.network.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.lore.data.LoreKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class PacketNotification implements IMessage {

    public static final byte TYPE_CLIENT_PICKUP = 0;
    public static final byte TYPE_CLIENT_AUTOPLAY = 1;
    public static final byte TYPE_SERVER_AUTOPLAY_CONFIRM = 1;

    public static void notify(EntityPlayer entityPlayer, byte type, LoreKey loreKey) {
        PacketNotification packet = new PacketNotification();
        packet.type = type;
        packet.key = loreKey;

        if (type == TYPE_CLIENT_PICKUP || type == TYPE_CLIENT_AUTOPLAY) {
            PacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) entityPlayer);
        } else {
            PacketHandler.INSTANCE.sendToServer(packet);
        }
    }

    public byte type;
    public LoreKey key;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type);
        ByteBufUtils.writeUTF8String(buf, key.category);
        ByteBufUtils.writeUTF8String(buf, key.ident);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readByte();
        key = new LoreKey(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
    }

    public static class Handler implements IMessageHandler<PacketNotification, IMessage> {

        @Override
        public IMessage onMessage(PacketNotification message, MessageContext ctx) {
            World clientWorld = LoreExpansion.proxy.getClientWorld();
            if (clientWorld == null) {
                if (message.type == TYPE_CLIENT_PICKUP || message.type == TYPE_CLIENT_AUTOPLAY) {
                    LoreExpansion.proxy.handleNotificationPacket(message, ctx);
                }
            } else {
                if (message.type == TYPE_SERVER_AUTOPLAY_CONFIRM) {
                    LoreExpansion.proxy.handleNotificationPacket(message, ctx);
                }
            }

            return null;
        }
    }
}
