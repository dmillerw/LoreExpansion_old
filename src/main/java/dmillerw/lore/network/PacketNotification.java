package dmillerw.lore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class PacketNotification implements IMessage, IMessageHandler<PacketNotification, IMessage> {

	public static void notify(EntityPlayerMP player, int page, int dimension, byte type) {
		PacketHandler.INSTANCE.sendTo(new PacketNotification(page, dimension, type), player);
	}

	public static final byte PICKUP = 0;
	public static final byte AUTOPLAY = 1;

	public int page;
	public int dimension;

	public byte type;

	public PacketNotification() {

	}

	public PacketNotification(int page, int dimension, byte type) {
		this.page = page;
		this.dimension = dimension;
		this.type = type;
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
	public IMessage onMessage(PacketNotification message, MessageContext ctx) {
		World world = LoreExpansion.proxy.getClientWorld();
		if (world == null) {
			return null;
		}
		LoreExpansion.proxy.handleNotificationPacket(message);
		return null;
	}
}
