package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.lore.client.handler.KeyHandler;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new KeyHandler());
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
