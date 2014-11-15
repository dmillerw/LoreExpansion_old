package dmillerw.lore.common.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.lore.data.LoreKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class PacketServerNotification implements INotificationPacket, IMessage, IMessageHandler<PacketServerNotification, IMessage> {

    public static final byte CONFIRM_AUTOPLAY = 0;

    public int page;
    public int dimension;

    public byte type;

    public PacketServerNotification() {

    }

    public PacketServerNotification(int page, int dimension, byte type) {
        this.page = page;
        this.dimension = dimension;
        this.type = type;
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public LoreKey getData() {
        return new LoreKey(page, dimension);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(page);
        if (dimension == Integer.MAX_VALUE) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeInt(dimension);
        }
        buf.writeByte(type);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        page = buf.readInt();
        dimension = Integer.MAX_VALUE;
        if (buf.readBoolean()) {
            dimension = buf.readInt();
        }
        type = buf.readByte();
    }

    @Override
    public IMessage onMessage(PacketServerNotification message, MessageContext ctx) {
        World world = LoreExpansion.proxy.getClientWorld();
        if (world == null) {
            return null;
        }
        LoreExpansion.proxy.handleNotificationPacket(message, ctx);
        return null;
    }
}
