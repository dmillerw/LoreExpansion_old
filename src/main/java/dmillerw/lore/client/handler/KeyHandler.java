package dmillerw.lore.client.handler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.core.GuiHandler;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * @author dmillerw
 */
public class KeyHandler {

	public KeyBinding key = new KeyBinding("Lore Journal", Keyboard.KEY_L, "key.categories.misc");

	public KeyHandler() {
		ClientRegistry.registerKeyBinding(key);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			return;
		}
		if (event.phase == TickEvent.Phase.START) {
			if (key.getIsKeyPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
				event.player.openGui(LoreExpansion.instance, GuiHandler.GUI_JOURNAL, event.player.worldObj, 0, 0, 0);
			}
		}
	}

}
