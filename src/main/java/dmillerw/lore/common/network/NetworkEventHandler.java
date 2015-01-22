package dmillerw.lore.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.network.packet.PacketSyncLore;

/**
 * @author dmillerw
 */

public class NetworkEventHandler {

	@SubscribeEvent
	public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			PacketSyncLore.updateLore((EntityPlayerMP) player);
		}
	}
}
