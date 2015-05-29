package dmillerw.lore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.lore.common.core.handler.CommandDelayHandler;
import dmillerw.lore.common.core.handler.PlayerSpawnHandler;
import dmillerw.lore.common.core.handler.PlayerTickHandler;
import dmillerw.lore.common.item.ItemJournal;
import dmillerw.lore.common.item.ItemLorePage;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.entity.LoreProperties;
import dmillerw.lore.common.network.NetworkEventHandler;
import dmillerw.lore.common.network.packet.PacketHandler;
import dmillerw.lore.common.network.packet.PacketNotification;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        LoreExpansion.lorePage = new ItemLorePage().setUnlocalizedName("page");
        GameRegistry.registerItem(LoreExpansion.lorePage, "page");

        LoreExpansion.journal = new ItemJournal().setUnlocalizedName("journal");
        GameRegistry.registerItem(LoreExpansion.journal, "journal");

        LoreLoader.initialize();

        PacketHandler.init();

        FMLCommonHandler.instance().bus().register(new NetworkEventHandler());
        FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
        FMLCommonHandler.instance().bus().register(new CommandDelayHandler());
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerSpawnHandler());
    }

    public void handleNotificationPacket(PacketNotification packet, MessageContext context) {
        if (packet.type == PacketNotification.TYPE_SERVER_AUTOPLAY_CONFIRM) {
            EntityPlayer player = context.getServerHandler().playerEntity;
            LoreKey key = packet.key;
            Lore lore = LoreLoader.getLore(key);

            LoreProperties properties = PlayerHandler.getCollectedLore(player);

            if (lore.autoplay && properties.canAutoplay(key)) {
                properties.setAutoplayed(key, true);
                PacketNotification.notify(player, PacketNotification.TYPE_CLIENT_AUTOPLAY, key);
            }
        }
    }

    public World getClientWorld() {
        return null;
    }
}
