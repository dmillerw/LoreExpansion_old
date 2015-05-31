package dmillerw.lore.common.network.packet;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * @author dmillerw
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("loreexp");

    public static void init() {
        INSTANCE.registerMessage(PacketNotification.Handler.class, PacketNotification.class, -1, Side.SERVER);
        INSTANCE.registerMessage(PacketNotification.Handler.class, PacketNotification.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(PacketSyncLore.Handler.class, PacketSyncLore.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(PacketReloadLore.Handler.class, PacketReloadLore.class, 3, Side.CLIENT);
    }
}
