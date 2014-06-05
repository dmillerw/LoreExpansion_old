package dmillerw.lore.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.PlayerHandler;

/**
 * @author dmillerw
 */
public class NetworkEventHandler {

	@SubscribeEvent
	public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PlayerHandler.clearLore(event.player);
			PlayerHandler.loadPlayerLore(event.player);

			try {
				if (LoreExpansion.proxy.getClientWorld() == null) {
					PacketHandler.INSTANCE.sendToAll(new PacketSyncLore(event.player, PlayerHandler.getLore(event.player)));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void playerUpdateEvent(PlayerEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PlayerHandler.savePlayerBaubles(event.player);
		}
	}

}
