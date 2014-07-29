package dmillerw.lore.client.gui;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.client.sound.SoundHandler;
import dmillerw.lore.client.texture.SubTexture;
import dmillerw.lore.ClientProxy;
import dmillerw.lore.common.lore.LoreLoader;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.misc.Pair;
import dmillerw.lore.common.misc.StringHelper;
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

	private static final SubTexture LORE_BOX_SELECTED = new SubTexture(JOURNAL_LEFT, 185, 45, 18, 18);
	private static final SubTexture LORE_BOX_ACTIVE = new SubTexture(JOURNAL_LEFT, 185, 68, 18, 18);
	private static final SubTexture ARROW_DIMENSION_BACK = new SubTexture(JOURNAL_LEFT, 191, 162, 7, 8);
	private static final SubTexture ARROW_DIMENSION_BACK_MOUSEOVER = new SubTexture(JOURNAL_LEFT, 191, 175, 7, 8);
	private static final SubTexture ARROW_DIMENSION_FORWARD = new SubTexture(JOURNAL_LEFT, 205, 162, 7, 8);
	private static final SubTexture ARROW_DIMENSION_FORWARD_MOUSEOVER = new SubTexture(JOURNAL_LEFT, 205, 175, 7, 8);
	private static final SubTexture ARROW_PAGE_BACK = new SubTexture(JOURNAL_LEFT, 186, 191, 12, 12);
	private static final SubTexture ARROW_PAGE_BACK_MOUSEOVER = new SubTexture(JOURNAL_LEFT, 186, 207, 12, 12);
	private static final SubTexture ARROW_PAGE_FORWARD = new SubTexture(JOURNAL_LEFT, 205, 191, 12, 12);
	private static final SubTexture ARROW_PAGE_FORWARD_MOUSEOVER = new SubTexture(JOURNAL_LEFT, 205, 207, 12, 12);
	private static final SubTexture ARROW_SCROLL_UP = new SubTexture(JOURNAL_RIGHT, 168, 37, 13, 6);
	private static final SubTexture ARROW_SCROLL_DOWN = new SubTexture(JOURNAL_RIGHT, 168, 203, 13, 6);
	private static final SubTexture PLAY_BUTTON_ACTIVE = new SubTexture(JOURNAL_RIGHT, 177, 191, 4, 7);
	private static final SubTexture PLAY_BUTTON_INACTIVE = new SubTexture(JOURNAL_RIGHT, 177, 184, 4, 7);
	private static final SubTexture PAUSE_BUTTON_ACTIVE = new SubTexture(JOURNAL_RIGHT, 183, 192, 5, 5);
	private static final SubTexture PAUSE_BUTTON_INACTIVE = new SubTexture(JOURNAL_RIGHT, 177, 185, 5, 5);
	private static final SubTexture STOP_BUTTON_ACTIVE = new SubTexture(JOURNAL_RIGHT, 170, 192, 5, 5);
	private static final SubTexture STOP_BUTTON_INACTIVE = new SubTexture(JOURNAL_RIGHT, 170, 185, 5, 5);

	private static final Pair<Integer, Integer> LEFT_SIZE = new Pair<Integer, Integer>(175, 230);
	private static final Pair<Integer, Integer> RIGHT_SIZE = new Pair<Integer, Integer>(168, 230);
	private static final Pair<Integer, Integer> TOTAL_SIZE = new Pair<Integer, Integer>(LEFT_SIZE.left + RIGHT_SIZE.left, 230);
	private static final Pair<Integer, Integer> BOX_START = new Pair<Integer, Integer>(35, 45);
	private static final Pair<Integer, Integer> TAB_SIZE = new Pair<Integer, Integer>(15, 26);
	private static final Pair<Integer, Integer> TAB_BACK = new Pair<Integer, Integer>(0, 16);
	private static final Pair<Integer, Integer> TAB_FORWARD = new Pair<Integer, Integer>(0, 46);
	private static final Pair<Integer, Integer> ARROW_DIMENSION_BACK_POS = new Pair<Integer, Integer>(5, 25);
	private static final Pair<Integer, Integer> ARROW_DIMENSION_FORWARD_POS = new Pair<Integer, Integer>(5, 55);
	private static final Pair<Integer, Integer> ARROW_PAGE_SIZE = new Pair<Integer, Integer>(12, 12);
	private static final Pair<Integer, Integer> ARROW_PAGE_BACK_POS = new Pair<Integer, Integer>(20, 206);
	private static final Pair<Integer, Integer> ARROW_PAGE_FORWARD_POS = new Pair<Integer, Integer>(148, 206);
	private static final Pair<Integer, Integer> PLAY_POS = new Pair<Integer, Integer>(122, 203);
	private static final Pair<Integer, Integer> PAUSE_POS = new Pair<Integer, Integer>(122, 204);
	private static final Pair<Integer, Integer> STOP_POS = new Pair<Integer, Integer>(41, 204);

	private static final float TEXT_SCALE = 1F;

	private static final int INDENTATION = 3;
	private static final int TITLE_Y = 24;
	private static final int BODY_X = 20;
	private static final int BODY_y = 35;
	private static final int LORE_BOX_GAP = 23;
	private static final int LORE_ROW_COUNT = 17;
	private static final int ARROW_SCROLL_X = 78;
	private static final int ARROW_SCROLL_UP_Y = 37;
	private static final int ARROW_SCROLL_DOWN_Y = 203;

	public static int maxPage = 0;

	public static int loreScrollIndex = 0;
	public static int textScrollIndex = 0;
	public static List<LoreKey> playerLore = new ArrayList<LoreKey>();
	public static LoreKey selectedLore;

	private static int[] dimensions = new int[] {Integer.MAX_VALUE};

	private static int dimensionIndex = Integer.MIN_VALUE;

	private List<String> currentLore = new ArrayList<String>();

	private final EntityPlayer player;

	public GuiJournal(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void initGui() {
		// Fill dimensions array and set index
		int[] dims = LoreLoader.INSTANCE.getAllDimensions();
		if (dims.length > 0) {
			GuiJournal.dimensions = dims;
		}

		if (dimensionIndex == Integer.MIN_VALUE || dimensionIndex >= dimensions.length) {
			dimensionIndex = 0;
		}

        // Why was I resetting...? :/
        //TODO Analyze
//		reset();

		// Open selected page
		if (selectedLore != null) {
			for (int i=0; i<dimensions.length; i++) {
				if (dimensions[i] == selectedLore.dimension) {
					dimensionIndex = i;
					loadLore(selectedLore);
					ClientProxy.pickedUpPage = null;
					return;
				}
			}
			selectedLore = null;
			ClientProxy.pickedUpPage = null;
		}
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		onWheelScrolled(x, y, Mouse.getDWheel());

		// VARIABLES
		int left = (width - TOTAL_SIZE.left) / 2;
		int top = (height - TOTAL_SIZE.right) / 2;
		Lore current = null;
		if (selectedLore != null) {
			current = LoreLoader.INSTANCE.getLore(selectedLore);

			if (current == null) {
				selectedLore = null;
			}
		}
		Lore[] all = LoreLoader.INSTANCE.getAllLore();
		int dimension = dimensions[dimensionIndex];

		// BACKGROUND
		GL11.glColor4f(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(JOURNAL_LEFT);
		drawTexturedModalRect(left, top, 0, 0, LEFT_SIZE.left, LEFT_SIZE.right);
		mc.getTextureManager().bindTexture(JOURNAL_RIGHT);
		drawTexturedModalRect(left + LEFT_SIZE.left, top, 0, 0, RIGHT_SIZE.left, RIGHT_SIZE.right);

		// LORE BACKGROUNDS
		for (Lore lore : all) {
			if (lore != null && lore.validDimension(dimension)) {
				int page = lore.page - (4 * loreScrollIndex);

				if (page > 0 && page < 35) {
					int drawX = (((page - 1) % 4) * LORE_BOX_GAP);
					int drawY = 0;
					if ((page - 1) > 4) {
						drawY = (((page - 1) / 4) * LORE_BOX_GAP);
					}

					if (selectedLore != null && selectedLore.page == lore.page) {
						LORE_BOX_ACTIVE.draw(left + BOX_START.left + drawX, top + BOX_START.right + drawY, (int) zLevel);
					} else {
						LORE_BOX_SELECTED.draw(left + BOX_START.left + drawX, top + BOX_START.right + drawY, (int) zLevel);
					}
				}
			}
		}

		// LORE ICONS
		mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		IIcon icon = LoreExpansion.lorePage.getIconFromDamage(0);
		for (LoreKey key : playerLore) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);
			if (lore != null && lore.validDimension(dimension)) {
				int page = lore.page - (4 * loreScrollIndex);

				if (page > 0 && page < 35) {
					int drawX = (((page - 1) % 4) * LORE_BOX_GAP);
					int drawY = 0;
					if ((page - 1) > 4) {
						drawY = (((page - 1) / 4) * LORE_BOX_GAP);
					}

					drawTexturedModelRectFromIcon(left + BOX_START.left + drawX + 1, top + BOX_START.right + drawY + 1, icon, 16, 16);
				}
			}
		}

		// AUDIO CONTROL
		if (current != null && !current.sound.isEmpty()) {
			String sound = current.sound;
			if (SoundHandler.INSTANCE.isPlaying(sound)) {
				STOP_BUTTON_ACTIVE.draw(left + LEFT_SIZE.left + STOP_POS.left, top + STOP_POS.right, (int)zLevel);
				if (SoundHandler.INSTANCE.isPaused()) {
					PLAY_BUTTON_ACTIVE.draw(left + LEFT_SIZE.left + PLAY_POS.left, top + PLAY_POS.right, (int)zLevel);
				} else {
					PAUSE_BUTTON_ACTIVE.draw(left + LEFT_SIZE.left + PAUSE_POS.left, top + PAUSE_POS.right, (int)zLevel);
				}
			} else {
				STOP_BUTTON_INACTIVE.draw(left + LEFT_SIZE.left + STOP_POS.left, top + STOP_POS.right, (int)zLevel);
				PLAY_BUTTON_ACTIVE.draw(left + LEFT_SIZE.left + PLAY_POS.left, top + PLAY_POS.right, (int)zLevel);
			}
		} else {
			STOP_BUTTON_INACTIVE.draw(left + LEFT_SIZE.left + STOP_POS.left, top + STOP_POS.right, (int)zLevel);
			PLAY_BUTTON_INACTIVE.draw(left + LEFT_SIZE.left + PLAY_POS.left, top + PLAY_POS.right, (int)zLevel);
		}

		// ARROWS - SCROLL
		GL11.glColor4f(1, 1, 1, 1);
		if (current != null) {
			if (textScrollIndex > 0) {
				ARROW_SCROLL_UP.draw(left + LEFT_SIZE.left + ARROW_SCROLL_X, top + ARROW_SCROLL_UP_Y, (int)zLevel);
			}

			if (currentLore.size() - LORE_ROW_COUNT > textScrollIndex) {
				ARROW_SCROLL_DOWN.draw(left + LEFT_SIZE.left + ARROW_SCROLL_X, top + ARROW_SCROLL_DOWN_Y, (int)zLevel);
			}
		}

		// ARROWS - DIMENSION
		if (inBounds(left + TAB_BACK.left, top + TAB_BACK.right, TAB_SIZE.left, TAB_SIZE.right, x, y)) {
			ARROW_DIMENSION_BACK_MOUSEOVER.draw(left + ARROW_DIMENSION_BACK_POS.left, top + ARROW_DIMENSION_BACK_POS.right, (int)zLevel);
		} else {
			ARROW_DIMENSION_BACK.draw(left + ARROW_DIMENSION_BACK_POS.left, top + ARROW_DIMENSION_BACK_POS.right, (int)zLevel);
		}

		if (inBounds(left + TAB_FORWARD.left, top + TAB_FORWARD.right, TAB_SIZE.left, TAB_SIZE.right, x, y)) {
			ARROW_DIMENSION_FORWARD_MOUSEOVER.draw(left + ARROW_DIMENSION_FORWARD_POS.left, top + ARROW_DIMENSION_FORWARD_POS.right, (int)zLevel);
		} else {
			ARROW_DIMENSION_FORWARD.draw(left + ARROW_DIMENSION_FORWARD_POS.left, top + ARROW_DIMENSION_FORWARD_POS.right, (int)zLevel);
		}

		// ARROWS - PAGE
//		if (inBounds(left + ARROW_PAGE_BACK_POS.left, top + ARROW_PAGE_BACK_POS.right, ARROW_PAGE_SIZE.left, ARROW_PAGE_SIZE.right, x, y)) {
//			ARROW_PAGE_BACK_MOUSEOVER.draw(left + ARROW_PAGE_BACK_POS.left, top + ARROW_PAGE_BACK_POS.right, (int)zLevel);
//		} else {
//			ARROW_PAGE_BACK.draw(left + ARROW_PAGE_BACK_POS.left, top + ARROW_PAGE_BACK_POS.right, (int)zLevel);
//		}

//		if (inBounds(left + ARROW_PAGE_FORWARD_POS.left, top + ARROW_PAGE_FORWARD_POS.right, ARROW_PAGE_SIZE.left, ARROW_PAGE_SIZE.right, x, y)) {
//			ARROW_PAGE_FORWARD_MOUSEOVER.draw(left + ARROW_PAGE_FORWARD_POS.left, top + ARROW_PAGE_FORWARD_POS.right, (int)zLevel);
//		} else {
//			ARROW_PAGE_FORWARD.draw(left + ARROW_PAGE_FORWARD_POS.left, top + ARROW_PAGE_FORWARD_POS.right, (int)zLevel);
//		}

		// LORE TOOLTIPS
		for (LoreKey key : playerLore) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);
			if (lore != null && lore.validDimension(dimension)) {
				int page = lore.page - (4 * loreScrollIndex);

				if (page > 0 && page < 35) {
					int drawX = (((page - 1) % 4) * LORE_BOX_GAP);
					int drawY = 0;
					if ((page - 1) > 4) {
						drawY = (((page - 1) / 4) * LORE_BOX_GAP);
					}

					if (inBounds(left + BOX_START.left + drawX, top + BOX_START.right + drawY, 16, 16, x, y)) {
						drawHoveringText(Arrays.asList(lore.title), x, y, mc.fontRenderer);
					}
				}
			}
		}

		// TEXT - LEFT
		String tag = LoreLoader.INSTANCE.getLoreTag(dimension);
		drawCenteredString(tag, (left + (LEFT_SIZE.left - 8) / 2) + 8, top + TITLE_Y, 0x000000);

		// TEXT - RIGHT
		if (current != null) {
			drawCenteredString(current.title, left + LEFT_SIZE.left + (RIGHT_SIZE.left / 2), top + TITLE_Y, 0x000000);
			for (int i= textScrollIndex; i<Math.min(textScrollIndex + LORE_ROW_COUNT, currentLore.size()); i++) {
				String lore = currentLore.get(i);
				drawString(lore, left + LEFT_SIZE.left + BODY_X, (top + BODY_y + ClientProxy.renderer.FONT_HEIGHT) + ClientProxy.renderer.FONT_HEIGHT * (i - textScrollIndex), TEXT_SCALE, 0x000000, true);
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		int left = (width - TOTAL_SIZE.left) / 2;
		int top = (height - TOTAL_SIZE.right) / 2;
		Lore current = null;
		if (selectedLore != null) {
			current = LoreLoader.INSTANCE.getLore(selectedLore);

			if (current == null) {
				selectedLore = null;
			}
		}
		int dimension = dimensions[dimensionIndex];

		for (LoreKey key : playerLore) {
			Lore lore = LoreLoader.INSTANCE.getLore(key);
			if (lore != null && lore.validDimension(dimension)) {
				int page = lore.page - (4 * loreScrollIndex);

				if (page > 0 && page < 35) {
					int drawX = (((page - 1) % 4) * LORE_BOX_GAP);
					int drawY = 0;
					if ((page - 1) > 4) {
						drawY = (((page - 1) / 4) * LORE_BOX_GAP);
					}

					if (inBounds(left + BOX_START.left + drawX, top + BOX_START.right + drawY, 16, 16, x, y)) {
						selectedLore = new LoreKey(lore.page, dimension);
						loadLore(selectedLore);
					}
				}
			}
		}

		// AUDIO CONTROL
		if (current != null) {
			String sound = current.sound;
			if (!sound.isEmpty()) {
				if (SoundHandler.INSTANCE.isPlaying(sound)) {
					if (inBounds(left + LEFT_SIZE.left + STOP_POS.left, top + STOP_POS.right, 5, 5, x, y)) {
						SoundHandler.INSTANCE.stop();
					} else if (inBounds(left + LEFT_SIZE.left + PLAY_POS.left, top + PLAY_POS.right, 4, 7, x, y)) {
						if (!SoundHandler.INSTANCE.isPaused()) {
							SoundHandler.INSTANCE.pause();
						} else {
							SoundHandler.INSTANCE.resume();
						}
					}
				} else if (inBounds(left + LEFT_SIZE.left + PLAY_POS.left, top + PLAY_POS.right, 4, 7, x, y)) {
					SoundHandler.INSTANCE.play(sound);
				}
			}
		}

		// ARROWS - SCROLL
		if (current != null) {
			if (inBounds(left + LEFT_SIZE.left + ARROW_SCROLL_X, top + ARROW_SCROLL_UP_Y, 13, 16, x, y)) {
				scrollText(-1);
			}

			if (inBounds(left + LEFT_SIZE.left + ARROW_SCROLL_X, top + ARROW_SCROLL_DOWN_Y, 13, 16, x, y)) {
				scrollText(1);
			}
		}

		// ARROWS - DIMENSION
		if (inBounds(left + TAB_BACK.left, top + TAB_BACK.right, TAB_SIZE.left, TAB_SIZE.right, x, y)) {
			int lastIndex = dimensionIndex;
			if (dimensionIndex <= 0) {
				dimensionIndex = dimensions.length - 1;
			} else {
				dimensionIndex--;
			}

			if (lastIndex != dimensionIndex) {
				reset();
			}
		}

		if (inBounds(left + TAB_FORWARD.left, top + TAB_FORWARD.right, TAB_SIZE.left, TAB_SIZE.right, x, y)) {
			int lastIndex = dimensionIndex;
			if (dimensionIndex >= dimensions.length - 1) {
				dimensionIndex = 0;
			} else {
				dimensionIndex++;
			}

			if (lastIndex != dimensionIndex) {
				reset();
			}
		}
	}

	public void reset() {
		int max = 0;
		for (Lore lore : LoreLoader.INSTANCE.getAllLore()) {
			if (lore != null && lore.validDimension(dimensions[dimensionIndex]) && lore.page > max) {
				max = lore.page;
			}
		}
		maxPage = max;
		selectedLore = null;
		textScrollIndex = 0;
		currentLore.clear();
		SoundHandler.INSTANCE.stop();
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
				str = StringHelper.indent(INDENTATION) + str;
				newList.add(str);
				if (i != lore.length - 1) {
					newList.add("");
				}
			}
		}

		for (String str : newList) {
			currentLore.addAll(ClientProxy.renderer.listFormattedStringToWidth(str, (int) (((RIGHT_SIZE.left) - 45) / TEXT_SCALE)));
		}
	}

	public void onWheelScrolled(int x, int y, int wheel) {
		int left = (width - TOTAL_SIZE.left) / 2;
		int top = (height - TOTAL_SIZE.right) / 2;

		if (inBounds(left, top, LEFT_SIZE.left, LEFT_SIZE.right, x, y)) {
//			scrollLore(-wheel);
		} else if (inBounds(left + LEFT_SIZE.left, top, RIGHT_SIZE.left, RIGHT_SIZE.right, x, y)) {
			scrollText(-wheel);
		}
	}

	@Override
	protected void keyTyped(char key, int code) {
		super.keyTyped(key, code);

		if (code == mc.gameSettings.keyBindInventory.getKeyCode()) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		if (code == Keyboard.KEY_UP) {
			scrollText(-1);
		}

		if (code == Keyboard.KEY_DOWN) {
			scrollText(1);
		}
	}

	public void scrollLore(int theta) {
		if (theta < 0) {
			loreScrollIndex--;
			if (loreScrollIndex < 0) {
				loreScrollIndex = 0;
			}
		}

		if (theta > 0) {
			loreScrollIndex++;
			if (loreScrollIndex > Math.max(0, Math.ceil(maxPage / 5))) {
				loreScrollIndex = (int)Math.max(0, Math.ceil(maxPage / 5));
			}
		}
	}

	public void scrollText(int theta) {
		if (theta < 0) {
			textScrollIndex -= 2;
			if (textScrollIndex < 0) {
				textScrollIndex = 0;
			}
		}

		if (theta > 0) {
			textScrollIndex += 2;
			if (textScrollIndex > Math.max(0, currentLore.size() - LORE_ROW_COUNT)) {
				textScrollIndex = Math.max(0, currentLore.size() - LORE_ROW_COUNT);
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

	public void drawCenteredString(String str, int x, int y, int color) {
		drawString(str, x - mc.fontRenderer.getStringWidth(str) / 2, y, color);
	}

	public void drawString(String str, int x, int y, int color) {
		drawString(str, x, y, 1.0F, color, false);
	}

	public void drawString(String str, int x, int y, float mult, int color, boolean custom) {
		GL11.glPushMatrix();
		GL11.glScalef(mult, mult, 1.0F);
		if (custom) {
			ClientProxy.renderer.drawString(str, (int)((x) / mult), (int)((y) / mult), color);
		} else {
			mc.fontRenderer.drawString(str, (int)((x) / mult), (int)((y) / mult), color);
		}
		GL11.glPopMatrix();
	}
}
