package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.core.handler.KeyHandler;
import dmillerw.lore.network.PacketNotifyOfPickup;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	public static int pickedUpPage = -1;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(SoundHandler.INSTANCE);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

	}

	@Override
	public void handlePickupPacket(PacketNotifyOfPickup packet) {
		pickedUpPage = packet.page;
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
