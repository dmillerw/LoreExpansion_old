package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.core.handler.KeyHandler;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		for (LoreData data : LoreLoader.INSTANCE.getLore()) {
			if (data != null) {
				data.preloadSounds();
			}
		}
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
