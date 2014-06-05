package dmillerw.lore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.PlayerHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class PacketSyncLore implements IMessage, IMessageHandler<PacketSyncLore, IMessage> {

	private int player;

	private List<Integer> lore;

	public PacketSyncLore() {

	}

	public PacketSyncLore(EntityPlayer player, List<Integer> lore) {
		this.lore = lore;
		this.player = player.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(player);
		buf.writeInt(lore.size());
		if (!lore.isEmpty()) {
			for (int i=0; i<lore.size(); i++) {
				buf.writeInt(lore.get(i));
			}
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		player = buf.readInt();
		lore = new ArrayList<Integer>();
		int size = buf.readInt();
		for (int i=0; i<size; i++) {
			lore.add(buf.readInt());
		}
	}

	@Override
	public IMessage onMessage(PacketSyncLore message, MessageContext ctx) {
		World world = LoreExpansion.proxy.getClientWorld();
		if (world == null) {
			return null;

		}
		Entity entity = world.getEntityByID(message.player);
		if (entity != null && entity instanceof EntityPlayer) {
			PlayerHandler.setLore((EntityPlayer) entity, lore);
		}
		return null;
	}

}
