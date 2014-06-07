package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.handler.ClientTickHandler;
import dmillerw.lore.core.handler.KeyHandler;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(new File(LoreExpansion.configFolder, "config.cfg"));
		config.load();
		boolean preload = config.get(Configuration.CATEGORY_GENERAL, "preload", false, "Preload all lore sounds when the game starts. Will induce additional startup lag, but prevents lag during game.").getBoolean(false);
		config.save();

		FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
		if (preload) {
			FMLCommonHandler.instance().bus().register(new ClientTickHandler());
		}
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
