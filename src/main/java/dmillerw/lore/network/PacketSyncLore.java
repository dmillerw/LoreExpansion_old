package dmillerw.lore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.gui.GuiJournal;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.lore.PlayerHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class PacketSyncLore implements IMessage, IMessageHandler<PacketSyncLore, IMessage> {

	public static void updateLore(EntityPlayerMP player) {
		PacketHandler.INSTANCE.sendTo(new PacketSyncLore(player, PlayerHandler.getLore(player)), player);
	}

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
		lore = new ArrayList<Integer>();
		player = buf.readInt();
		int size = buf.readInt();
		for (int i=0; i<size; i++) {
			int data = buf.readInt();
			this.lore.add(data);
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
			GuiJournal.loreCache = message.lore;
			if (!GuiJournal.loreCache.contains(GuiJournal.selectedLore)) {
				GuiJournal.selectedLore = -1;
				SoundHandler.INSTANCE.stop(); // Just in case
			}
		}
		return null;
	}

}
