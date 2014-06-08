package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.core.handler.KeyHandler;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketConfirmAutoplay;
import dmillerw.lore.network.PacketHandler;
import dmillerw.lore.network.PacketNotification;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	public static LoreKey pickedUpPage;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(SoundHandler.INSTANCE);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Override
	public void handleNotificationPacket(PacketNotification packet) {
		if (packet.type == PacketNotification.PICKUP) {
			ClientProxy.pickedUpPage = new LoreKey(packet.page, packet.dimension);
			PacketHandler.INSTANCE.sendToServer(new PacketConfirmAutoplay(packet.page, packet.dimension));
		}

		if (packet.type == PacketNotification.AUTOPLAY) {
			LoreKey key = new LoreKey(packet.page, packet.dimension);
			Lore lore = LoreLoader.INSTANCE.getLore(key);

			if (!lore.sound.isEmpty()) {
				SoundHandler.INSTANCE.play(lore.sound);
			}
		}
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
