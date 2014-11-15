package dmillerw.lore;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.PlayerHandler;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.entity.LoreProperties;
import dmillerw.lore.common.network.packet.INotificationPacket;
import dmillerw.lore.common.network.packet.PacketClientNotification;
import dmillerw.lore.common.network.packet.PacketHandler;
import dmillerw.lore.common.network.packet.PacketServerNotification;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void handleNotificationPacket(INotificationPacket packet, MessageContext context) {
        if (packet.getType() == PacketServerNotification.CONFIRM_AUTOPLAY) {
            EntityPlayer player = context.getServerHandler().playerEntity;
            LoreKey key = packet.getData();
            Lore lore = LoreLoader.INSTANCE.getLore(key);

            LoreProperties properties = PlayerHandler.getCollectedLore(player);

            if (lore.autoplay && properties.canAutoplay(key)) {
                properties.setAutoplayed(key, true);
                PacketHandler.INSTANCE.sendTo(new PacketClientNotification(key.page, key.dimension, PacketClientNotification.AUTOPLAY), (EntityPlayerMP) player);
            }
        }
    }

    public World getClientWorld() {
        return null;
    }
}
