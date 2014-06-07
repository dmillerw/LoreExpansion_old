package dmillerw.lore.core.proxy;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.network.PacketNotifyOfPickup;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void handlePickupPacket(PacketNotifyOfPickup packet) {
		// Shouldn't ever happen, and does nothing anyway :D
	}

	public World getClientWorld() {
		return null;
	}

}
