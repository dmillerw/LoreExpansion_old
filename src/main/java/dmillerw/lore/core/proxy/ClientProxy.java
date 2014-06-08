package dmillerw.lore.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.client.texture.SmallFontRenderer;
import dmillerw.lore.core.handler.KeyHandler;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketHandler;
import dmillerw.lore.network.PacketNotification;
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
	public void handleNotificationPacket(PacketNotification packet, MessageContext context) {
		if (packet.type == PacketNotification.Client.PICKUP) {
			ClientProxy.pickedUpPage = new LoreKey(packet.page, packet.dimension);
			PacketHandler.INSTANCE.sendToServer(new PacketNotification(packet.page, packet.dimension, PacketNotification.Server.CONFIRM_AUTOPLAY));
		}

		if (packet.type == PacketNotification.Client.AUTOPLAY) {
			LoreKey key = new LoreKey(packet.page, packet.dimension);
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
