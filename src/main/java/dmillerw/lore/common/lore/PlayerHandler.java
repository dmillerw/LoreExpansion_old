package dmillerw.lore.common.lore;

import dmillerw.lore.common.lore.data.entity.LoreProperties;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author dmillerw
 */
public class PlayerHandler {

	public static final String LORE_PROPERTIES = "loreexp:collected_lore";

	public static void attach(EntityPlayer player) {
		player.registerExtendedProperties(LORE_PROPERTIES, new LoreProperties());
	}

	public static LoreProperties getCollectedLore(EntityPlayer player) {
		return (LoreProperties) player.getExtendedProperties(LORE_PROPERTIES);
	}

}
