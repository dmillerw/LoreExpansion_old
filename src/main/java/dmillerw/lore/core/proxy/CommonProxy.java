package dmillerw.lore.core.proxy;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.command.LoreCommandSender;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.PlayerHandler;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketNotification;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author dmillerw
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void handleNotificationPacket(PacketNotification packet, MessageContext context) {
		if (packet.type == PacketNotification.Server.CONFIRM_AUTOPLAY) {
			EntityPlayer player = context.getServerHandler().playerEntity;
			LoreKey key = new LoreKey(packet.page, packet.dimension);
			Lore lore = LoreLoader.INSTANCE.getLore(key);
			List<LoreKey> lock = PlayerHandler.getLoreLock(player);
			if (lore.autoplay && !lock.contains(key)) {
				lock.add(key);
				PlayerHandler.setLoreLock(player, lock);
				PacketNotification.notify(key.page, key.dimension, PacketNotification.Client.AUTOPLAY);
			}
		}
	}

	public World getClientWorld() {
		return null;
	}

}
