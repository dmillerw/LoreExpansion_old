package dmillerw.lore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * @author dmillerw
 */
public class PacketConfirmAutoplay implements IMessage, IMessageHandler<PacketConfirmAutoplay, PacketNotification> {

	public int page;
	public int dimension;

	public PacketConfirmAutoplay() {

	}

	public PacketConfirmAutoplay(int page, int dimension) {
		this.page = page;
		this.dimension = dimension;
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
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		page = buf.readInt();
		dimension = Integer.MAX_VALUE;
		if (buf.readBoolean()) {
			dimension = buf.readInt();
		}
	}

	@Override
	public PacketNotification onMessage(PacketConfirmAutoplay message, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		LoreKey key = new LoreKey(message.page, message.dimension);
		Lore lore = LoreLoader.INSTANCE.getLore(key);
		List<LoreKey> lock = PlayerHandler.getLoreLock(player);
		if (lore.autoplay && !lock.contains(key)) {
			lock.add(key);
			PlayerHandler.setLoreLock(player, lock);
			return new PacketNotification(message.page, message.dimension, PacketNotification.AUTOPLAY);
		}
		return null;
	}
}
