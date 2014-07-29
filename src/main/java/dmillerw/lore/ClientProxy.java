package dmillerw.lore;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.client.texture.SmallFontRenderer;
import dmillerw.lore.common.core.handler.KeyHandler;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.network.packet.INotificationPacket;
import dmillerw.lore.common.network.packet.PacketClientNotification;
import dmillerw.lore.common.network.packet.PacketHandler;
import dmillerw.lore.common.network.packet.PacketServerNotification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

	public static SmallFontRenderer renderer;

	public static LoreKey pickedUpPage;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(SoundHandler.INSTANCE);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		renderer = new SmallFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("minecraft:textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
	}

	@Override
	public void handleNotificationPacket(INotificationPacket packet, MessageContext context) {
		LoreKey key = packet.getData();
		if (packet.getType() == PacketClientNotification.PICKUP) {
			ClientProxy.pickedUpPage = new LoreKey(key.page, key.dimension);
			PacketHandler.INSTANCE.sendToServer(new PacketServerNotification(key.page, key.dimension, PacketServerNotification.CONFIRM_AUTOPLAY));
		}

		if (packet.getType() == PacketClientNotification.AUTOPLAY) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);
			if (!lore.sound.isEmpty()) {
				SoundHandler.INSTANCE.play(lore.sound);
			}
		}
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

}
