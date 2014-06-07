package dmillerw.lore.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * @author dmillerw
 */
public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("loreexp");

	public static void init() {
		INSTANCE.registerMessage(PacketSyncLore.class, PacketSyncLore.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(PacketNotifyOfPickup.class, PacketNotifyOfPickup.class, 1, Side.CLIENT);
	}

}
