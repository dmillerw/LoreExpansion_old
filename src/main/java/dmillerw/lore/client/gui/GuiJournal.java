package dmillerw.lore.client.gui;

import dmillerw.lore.lore.PlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dmillerw
 */
public class GuiJournal extends GuiScreen {

	private static final ResourceLocation GUI = new ResourceLocation("loreexp:textures/gui/journal.png");

	private static final int XSIZE = 336;
	private static final int YSIZE = 230;

	private final EntityPlayer player;

	public GuiJournal(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		// BACKGROUND
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI);
		int left = (width - XSIZE) / 2;
		int top = (height - YSIZE) / 2;
		this.drawTexturedModalRect(left, top, 0, 0, XSIZE, YSIZE);

		System.out.println(PlayerHandler.getLore(Minecraft.getMinecraft().thePlayer));
	}
}
