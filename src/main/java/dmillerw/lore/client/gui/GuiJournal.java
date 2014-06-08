package dmillerw.lore.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.core.proxy.ClientProxy;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.lore.data.Lore;
import dmillerw.lore.lore.data.LoreKey;
import dmillerw.lore.network.PacketNotification;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
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
	
	public static List<LoreKey> loreCache = new ArrayList<LoreKey>();

	public static LoreKey selectedLore = null;

	private static int selectedDimension = Integer.MIN_VALUE;

	private static int scrollIndex = 0;

	private final EntityPlayer player;

	private List<String> currentLore = new ArrayList<String>();

	public GuiJournal(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void initGui() {
		if (selectedLore != null) {
			loadLore(selectedLore);
			ClientProxy.pickedUpPage = null;
		}

		if (selectedDimension == Integer.MIN_VALUE) {
			selectedDimension = FMLClientHandler.instance().getClient().theWorld.provider.dimensionId;
		}
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

		Lore data = null;
		if (selectedLore != null) {
			data = LoreLoader.INSTANCE.getLore(selectedLore);
		}

		// TEXT RENDERING
		drawCenteredString(LoreLoader.INSTANCE.getLoreTag(dimension), left + (XSIZE / 4), top + TEXT_Y, 0x000000);
		if (data != null) {
			drawCenteredString(data.title, (int) (left + XSIZE * 0.75F), top + TEXT_Y, 0x000000);
			boolean unicodeCache = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(false);
			for (int i=scrollIndex; i<Math.min(scrollIndex + LORE_ROW_COUNT, currentLore.size()); i++) {
				String lore = currentLore.get(i);
				drawString(lore, left + 190, (top + 40 + fontRendererObj.FONT_HEIGHT) + fontRendererObj.FONT_HEIGHT * (i - scrollIndex), SCALE, 0x000000);
			}
			mc.fontRenderer.setUnicodeFlag(unicodeCache);
		}

		// ARROWS
		GL11.glColor4f(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(JOURNAL_RIGHT);
		if (data != null && scrollIndex > 0) {
			drawTexturedModalRect(left + XSIZE / 2 + 78, top + 37, 168, 37, 13, 6);
		}
		if (data != null && currentLore.size() - LORE_ROW_COUNT > scrollIndex) {
			drawTexturedModalRect(left + XSIZE / 2 + 78, top + 203, 168, 203, 13, 6);
		}

		// AUDIO CONTROL
		GL11.glColor4f(1, 1, 1, 1);
		if (data != null) {
			String sound = data.sound;

			if (!sound.isEmpty()) {
				if (SoundHandler.INSTANCE.isPlaying(sound)) {
					drawTexturedModalRect(left + XSIZE / 2 + 41, top + 204, 170, 192, 5, 5); // STOP
					if (SoundHandler.INSTANCE.isPaused()) {
						drawTexturedModalRect(left + XSIZE / 2 + 122, top + 204, 183, 185, 5, 5); // PAUSE
					} else {
						drawTexturedModalRect(left + XSIZE / 2 + 122, top + 204, 183, 192, 5, 5); // PAUSE
					}
				} else {
					drawTexturedModalRect(left + XSIZE / 2 + 41, top + 204, 170, 185, 5, 5); // STOP
					drawTexturedModalRect(left + XSIZE / 2 + 122, top + 203, 177, 191, 4, 7); // START
				}
			}
		}

		Lore[] allLore = LoreLoader.INSTANCE.getAllLore();

		// LORE ICON BACKGROUNDS
		GL11.glColor4f(1, 1, 1, 1);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GL11.glEnable(GL11.GL_BLEND);
		mc.getTextureManager().bindTexture(JOURNAL_LEFT);
		for (Lore lore : allLore) {
			if (lore != null && lore.validDimension(selectedDimension)) {
				int page = lore.page;

				int drawX = (((page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((page - 1) > 4) {
					drawY = (((page - 1) / 4) * SLOT_GAP);
				}

				if (selectedLore != null && page == selectedLore.page) {
					drawTexturedModalRect(startX + drawX - 1, startY + drawY - 1, 178, 68, 18, 18);
				} else {
					drawTexturedModalRect(startX + drawX - 1, startY + drawY - 1, 178, 45, 18, 18);
				}
			}
		}
		GL11.glDisable(GL11.GL_BLEND);

		// LORE ICONS
		GL11.glColor4f(1, 1, 1, 1);
		IIcon icon = LoreExpansion.loreScrap.getIconFromDamage(0);
		mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		for (LoreKey key : loreCache) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);

			if (lore.validDimension(selectedDimension)) {
				int drawX = (((key.page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((key.page - 1) > 4) {
					drawY = (((key.page - 1) / 4) * SLOT_GAP);
				}

				drawTexturedModelRectFromIcon(startX + drawX, startY + drawY, icon, 16, 16);
			}
		}

		// LORE TOOLTIPS
		GL11.glColor4f(1, 1, 1, 1);
		for (LoreKey key : loreCache) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);

			if (lore.validDimension(selectedDimension)) {
				int drawX = (((key.page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((key.page - 1) > 4) {
					drawY = (((key.page - 1) / 4) * SLOT_GAP);
				}

				if (inBounds(drawX, drawY, 16, 16, mouseX, mouseY)) {
					drawHoveringText(Arrays.asList(LoreLoader.INSTANCE.getLore(key).title), mouseX + startX, mouseY + startY, mc.fontRenderer);
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

		// LORE PICKING
		for (LoreKey key : loreCache) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);

			if (lore.validDimension(selectedDimension)) {
				int drawX = (((key.page - 1) % 4) * SLOT_GAP);
				int drawY = 0;

				if ((key.page - 1) > 4) {
					drawY = (((key.page - 1) / 4) * SLOT_GAP);
				}

				if (inBounds(drawX, drawY, 16, 16, mouseX, mouseY)) {
					PacketNotification.notify(lore.page, lore.dimension, PacketNotification.Server.CONFIRM_AUTOPLAY);
					loadLore(key);
					scrollIndex = 0;
					break;
				}
			}
		}

		// SCROLLING
		if (inBounds(left + XSIZE / 2 + 78, top + 37, 13, 16, x, y)) {
			scroll(-1);
		}
		if (inBounds(left + XSIZE / 2 + 78, top + 203, 13, 16, x, y)) {
			scroll(1);
		}

		// AUDIO CONTROL
		Lore data = null;
		if (selectedLore != null) {
			data = LoreLoader.INSTANCE.getLore(selectedLore);
		}

		if (data != null) {
			String sound = data.sound;

			if (data.hasSound()) {
				if (SoundHandler.INSTANCE.isPlaying(sound)) {
					if (inBounds(left + XSIZE / 2 + 41, top + 204, 5, 5, x, y)) {
						SoundHandler.INSTANCE.stop();
					} else if (inBounds(left + XSIZE / 2 + 122, top + 203, 4, 7, x, y)) {
						if (!SoundHandler.INSTANCE.isPaused()) {
							SoundHandler.INSTANCE.pause();
						} else {
							SoundHandler.INSTANCE.resume();
						}
					}
				} else {
					if (inBounds(left + XSIZE / 2 + 122, top + 203, 4, 7, x, y)) {
						SoundHandler.INSTANCE.play(sound);
					}
				}
			}
		}
	}

	public void loadLore(LoreKey key) {
		currentLore.clear();
		selectedLore = key;

		String[] lore = LoreLoader.INSTANCE.getLore(selectedLore).body.split("[\r\n]");
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
	}

	public void onWheelScrolled(int wheel) {
		scroll(-wheel);
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
