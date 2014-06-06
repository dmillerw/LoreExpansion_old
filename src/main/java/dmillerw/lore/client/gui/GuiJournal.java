package dmillerw.lore.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.lore.LoreData;
import dmillerw.lore.lore.LoreLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dmillerw
 */
public class GuiJournal extends GuiScreen {

	private static final ResourceLocation JOURNAL_LEFT = new ResourceLocation("loreexp:textures/gui/journal_left.png");
	private static final ResourceLocation JOURNAL_RIGHT = new ResourceLocation("loreexp:textures/gui/journal_right.png");

	private static final int XSIZE = 336;
	private static final int YSIZE = 230;
	private static final int XSTART = 29;
	private static final int YSTART = 46;
	private static final int TEXT_Y = 24;
	private static final int SLOT_GAP = 23;
	private static final int LORE_ROW_COUNT = 17;

	private static final float SCALE = 0.651F;
	
	public static List<Integer> loreCache = new ArrayList<Integer>();

	private static int selectedLore = -1;

	private final EntityPlayer player;

	private List<String> currentLore = new ArrayList<String>();

	private int scrollIndex = 0;

	public GuiJournal(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		onWheelScrolled(Mouse.getDWheel());

		// BACKGROUND
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (width - XSIZE) / 2;
		int top = (height - YSIZE) / 2;
		mc.getTextureManager().bindTexture(JOURNAL_LEFT);
		drawTexturedModalRect(left, top, 0, 0, XSIZE / 2, YSIZE);
		mc.getTextureManager().bindTexture(JOURNAL_RIGHT);
		drawTexturedModalRect(left + XSIZE / 2, top, 0, 0, XSIZE / 2, YSIZE);

		int startX = left + XSTART;
		int startY = top + YSTART;
		int mouseX = (x - startX);
		int mouseY = (y - startY);
		int dimension = FMLClientHandler.instance().getClient().theWorld.provider.dimensionId;

		String text = LoreLoader.INSTANCE.getTag(dimension);

		// TEXT RENDERING
		drawCenteredString(text, left + (XSIZE / 4), top + TEXT_Y, 0x000000);
		if (selectedLore >= 0) {
			drawCenteredString(LoreLoader.INSTANCE.getLore(selectedLore).getTitle(dimension), (int) (left + XSIZE * 0.75F), top + TEXT_Y, 0x000000);
			boolean unicodeCache = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(false);
			for (int i=scrollIndex; i<Math.min(scrollIndex + LORE_ROW_COUNT, currentLore.size()); i++) {
				String lore = currentLore.get(i);
				drawString(lore, left + 190, (top + 40 + fontRendererObj.FONT_HEIGHT) + fontRendererObj.FONT_HEIGHT * (i - scrollIndex), SCALE, 0x000000);
			}
			mc.fontRenderer.setUnicodeFlag(unicodeCache);
		}

		// LORE ICON BACKGROUNDS
		GL11.glColor4f(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(JOURNAL_LEFT);
		for (LoreData data : LoreLoader.INSTANCE.getLore()) {
			if (data != null && data.validForDimension(dimension)) {
				int page = data.page;

				int drawX = (((page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((page - 1) > 4) {
					drawY = (((page - 1) / 4) * SLOT_GAP);
				}

				drawTexturedModalRect(startX + drawX - 1, startY + drawY - 1, 178, 45, 18, 18);
			}
		}

		// LORE ICONS
		GL11.glColor4f(1, 1, 1, 1);
		IIcon icon = LoreExpansion.loreScrap.getIconFromDamage(0);
		mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		for (int page : loreCache) {
			LoreData data = LoreLoader.INSTANCE.getLore(page);

			if (data.validForDimension(dimension)) {
				int drawX = (((page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((page - 1) > 4) {
					drawY = (((page - 1) / 4) * SLOT_GAP);
				}

				drawTexturedModelRectFromIcon(startX + drawX, startY + drawY, icon, 16, 16);
			}
		}

		// LORE TOOLTIPS
		GL11.glColor4f(1, 1, 1, 1);
		for (int page : loreCache) {
			LoreData data = LoreLoader.INSTANCE.getLore(page);

			if (data.validForDimension(dimension)) {
				int drawX = (((page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((page - 1) > 4) {
					drawY = (((page - 1) / 4) * SLOT_GAP);
				}

				if (inBounds(drawX, drawY, 16, 16, mouseX, mouseY)) {
					drawHoveringText(Arrays.asList(LoreLoader.INSTANCE.getLore(page).getTitle(dimension)), mouseX + startX, mouseY + startY, mc.fontRenderer);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		if (button != 0) {
			return;
		}

		int left = (width - XSIZE) / 2;
		int top = (height - YSIZE) / 2;
		int startX = left + XSTART;
		int startY = top + YSTART;
		int mouseX = (x - startX);
		int mouseY = (y - startY);
		int dimension = FMLClientHandler.instance().getClient().theWorld.provider.dimensionId;

		for (int page : loreCache) {
			LoreData data = LoreLoader.INSTANCE.getLore(page);

			if (data.validForDimension(dimension)) {
				int drawX = (((page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((page - 1) > 4) {
					drawY = (((page - 1) / 4) * SLOT_GAP);
				}

				if (inBounds(drawX, drawY, 16, 16, mouseX, mouseY)) {
					currentLore.clear();
					selectedLore = page;

					String[] lore = LoreLoader.INSTANCE.getLore(selectedLore).getLore(dimension).split("[\r\n]");
					List<String> newList = new ArrayList<String>();

					for (int i=0; i<lore.length; i++) {
						String str = lore[i];

						if (!str.isEmpty()) {
							str = str.trim();
							str = str.replace("\t", "");
							str = "     " + str;
							newList.add(str);
							if (i != lore.length - 1) {
								newList.add("");
							}
						}
					}

					for (String str : newList) {
						currentLore.addAll(mc.fontRenderer.listFormattedStringToWidth(str, (int)(((XSIZE / 2) - 45) / SCALE)));
					}

					scrollIndex = 0;
					break;
				}
			}
		}
	}

	public void onWheelScrolled(int wheel) {
		scroll(wheel);
	}

	@Override
	protected void keyTyped(char key, int code) {
		super.keyTyped(key, code);

		if (code == mc.gameSettings.keyBindInventory.getKeyCode()) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		if (code == Keyboard.KEY_UP) {
			scroll(-1);
		}

		if (code == Keyboard.KEY_DOWN) {
			scroll(1);
		}
	}

	public void scroll(int theta) {
		if (theta < 0) {
			scrollIndex--;
			if (scrollIndex < 0) {
				scrollIndex = 0;
			}
		}

		if (theta > 0) {
			scrollIndex++;
			if (scrollIndex > Math.max(0, currentLore.size() - LORE_ROW_COUNT)) {
				scrollIndex = Math.max(0, currentLore.size() - LORE_ROW_COUNT);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public boolean inBounds(int x, int y, int w, int h, int mX, int mY) {
		return (x <= mX) && (mX <= x + w) && (y <= mY) && (mY <= y + h);
	}

	public void drawString(String str, int x, int y, int color) {
		drawString(str, x, y, 1.0F, color);
	}

	public void drawCenteredString(String str, int x, int y, int color) {
		drawString(str, x - fontRendererObj.getStringWidth(str) / 2, y, color);
	}

	public void drawString(String str, int x, int y, float mult, int color) {
		GL11.glPushMatrix();
		GL11.glScalef(mult, mult, 1.0F);
		fontRendererObj.drawString(str, (int) ((x) / mult), (int) ((y) / mult), color);
		GL11.glPopMatrix();
	}

	public void drawSplitString(String str, int x, int y, int max, float mult, int color) {
		GL11.glPushMatrix();
		GL11.glScalef(mult, mult, 1.0F);
		fontRendererObj.drawSplitString(str, (int) ((x) / mult), (int) ((y) / mult), (int) ((max) / mult), color);
		GL11.glPopMatrix();
	}
}
