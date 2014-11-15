package dmillerw.lore.common.core.handler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.gui.GuiJournal;
import dmillerw.lore.common.core.GuiHandler;
import dmillerw.lore.ClientProxy;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * @author dmillerw
 */
public class KeyHandler {

    public static final KeyHandler INSTANCE = new KeyHandler();

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
                if (ClientProxy.pickedUpPage != null) {
                    GuiJournal.selectedLore = ClientProxy.pickedUpPage;
                }
                event.player.openGui(LoreExpansion.instance, GuiHandler.GUI_JOURNAL, event.player.worldObj, 0, 0, 0);
            }
        }
    }
}
