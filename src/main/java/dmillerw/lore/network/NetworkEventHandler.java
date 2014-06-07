package dmillerw.lore.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.lore.PlayerHandler;
import net.minecraft.entity.player.EntityPlayerMP;

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
			PacketSyncLore.updateLore((EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PlayerHandler.savePlayerLore(event.player);
		}
	}

	@SubscribeEvent
	public void playerUpdateEvent(PlayerEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PlayerHandler.savePlayerLore(event.player);
		}
	}

}
