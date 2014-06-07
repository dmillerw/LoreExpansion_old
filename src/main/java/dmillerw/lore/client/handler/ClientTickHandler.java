package dmillerw.lore.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;

import java.util.concurrent.TimeUnit;

/**
 * @author dmillerw
 */
public class ClientTickHandler {

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		boolean shouldPreload = false;
		for (LoreData data : LoreLoader.INSTANCE.getLore()) {
			if (data != null && data.shouldPreload()) {
				shouldPreload = true;
			}
		}

		if (shouldPreload) {
			LoreExpansion.logger.info("Preloading sounds. This may take a while");
			long startTime = System.nanoTime();
			for (LoreData data : LoreLoader.INSTANCE.getLore()) {
				if (data != null) {
					data.preloadSounds();
				}
			}
			long endTime = System.nanoTime() - startTime;
			LoreExpansion.logger.info("Finished preloading sounds. Took " + TimeUnit.SECONDS.convert(endTime, TimeUnit.NANOSECONDS) + " seconds");
		}
	}

}
