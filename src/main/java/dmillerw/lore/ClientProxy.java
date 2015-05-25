package dmillerw.lore;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.client.gui.GuiJournal;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.client.texture.SmallFontRenderer;
import dmillerw.lore.common.core.handler.KeyHandler;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.network.packet.INotificationPacket;
import dmillerw.lore.common.network.packet.PacketClientNotification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    public static SmallFontRenderer renderer;

    public static LoreKey pickedUpPage;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        FMLCommonHandler.instance().bus().register(KeyHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(SoundHandler.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        renderer = new SmallFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("minecraft:textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    }

    @Override
    public void handleNotificationPacket(INotificationPacket packet, MessageContext context) {
        LoreKey key = packet.getData();
        if (packet.getType() == PacketClientNotification.PICKUP) {
            ClientProxy.pickedUpPage = key.copy();
            if (LoreLoader.getLore(key).notify) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("You've discovered a new lore page. Press " + Keyboard.getKeyName(KeyHandler.INSTANCE.key.getKeyCode()) + " to view"));
            }
//			PacketHandler.INSTANCE.sendToServer(new PacketServerNotification(key.page, key.dimension, PacketServerNotification.CONFIRM_AUTOPLAY));
        }

        if (packet.getType() == PacketClientNotification.AUTOPLAY) {
            Lore lore = LoreLoader.getLore(key);
            if (!lore.sound.isEmpty()) {
                SoundHandler.INSTANCE.play(lore.sound);

                // I feel like autoplaying lore should immediately be viewable in the journal, regardless
                // of whether or not they opened it via keybind.
                GuiJournal.selectedLore = key.copy();
                ClientProxy.pickedUpPage = null;
            }
        }
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
