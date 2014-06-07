package dmillerw.lore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author dmillerw
 */
public class PacketNotifyOfPickup implements IMessage, IMessageHandler<PacketNotifyOfPickup, IMessage> {

	public static void notify(EntityPlayerMP player, int page) {
		PacketHandler.INSTANCE.sendTo(new PacketNotifyOfPickup(page), player);
	}

	public int page;

	public PacketNotifyOfPickup() {

	}

	public PacketNotifyOfPickup(int page) {
		this.page = page;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(page);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		page = buf.readInt();
	}

	@Override
	public IMessage onMessage(PacketNotifyOfPickup message, MessageContext ctx) {
		LoreExpansion.proxy.handlePickupPacket(message);
		return null;
	}
}
