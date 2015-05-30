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
import dmillerw.lore.common.network.packet.PacketNotification;
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
    public void handleNotificationPacket(PacketNotification packet, MessageContext context) {
        Lore lore = LoreLoader.getLore(packet.key);
        if (lore == null) {
            LoreExpansion.logger.warn("Received a notification packet from the server involving an unknown piece of lore");
            LoreExpansion.logger.warn("Ensure your configs and lore files match up with the server you're connected to!");
            return;
        }

        if (packet.type == PacketNotification.TYPE_CLIENT_PICKUP) {
            ClientProxy.pickedUpPage = packet.key.copy();
            if (!lore.hidden && lore.notify) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("You've discovered a new lore page. Press " + Keyboard.getKeyName(KeyHandler.INSTANCE.key.getKeyCode()) + " to view"));
            }
        }

        if (packet.type == PacketNotification.TYPE_CLIENT_AUTOPLAY) {
            if (!lore.sound.isEmpty()) {
                SoundHandler.INSTANCE.play(lore.sound);

                if (!lore.hidden) {
                    // I feel like autoplaying lore should immediately be viewable in the journal, regardless
                    // of whether or not they opened it via keybind.
                    GuiJournal.selectedLore = packet.key.copy();
                    ClientProxy.pickedUpPage = null;
                }
            }
        }
    }

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
