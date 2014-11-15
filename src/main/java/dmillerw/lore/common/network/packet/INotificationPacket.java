package dmillerw.lore.common.network.packet;

import dmillerw.lore.common.lore.data.LoreKey;

/**
 * @author dmillerw
 */
public interface INotificationPacket {

    public byte getType();

    public LoreKey getData();
}
