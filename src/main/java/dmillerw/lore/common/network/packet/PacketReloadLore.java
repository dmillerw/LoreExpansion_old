package dmillerw.lore.common.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.lore.common.lore.LoreLoader;
import io.netty.buffer.ByteBuf;

/**
 * @author dmillerw
 */
public class PacketReloadLore implements IMessage {

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketReloadLore, IMessage> {

        @Override
        public IMessage onMessage(PacketReloadLore message, MessageContext ctx) {
            LoreLoader.clear();
            LoreLoader.initialize();
            return null;
        }
    }
}
