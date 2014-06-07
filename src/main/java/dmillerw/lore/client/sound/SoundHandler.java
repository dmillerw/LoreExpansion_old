package dmillerw.lore.client.sound;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import dmillerw.lore.LoreExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * @author dmillerw
 */
public class SoundHandler {

	public static final SoundHandler INSTANCE = new SoundHandler();

	private static final String[] SOUND_MANAGER_MAPPING = new String[] {"sndManager", "field_147694_f"};
	private static final String[] SOUND_SYSTEM_MAPPING = new String[] {"sndSystem", "field_148620_e"};

	private static boolean paused = false;

	private Map<String, String> nameToTempMap = Maps.newHashMap();

	private SoundManager soundManager;

	private SoundSystem soundSystem;

	private String nowPlaying = "";

	private boolean loaded = false;

	private void initialize() {
		try {
			soundManager = (SoundManager) ReflectionHelper.findField(net.minecraft.client.audio.SoundHandler.class, SOUND_MANAGER_MAPPING).get(Minecraft.getMinecraft().getSoundHandler());
			soundSystem = (SoundSystem) ReflectionHelper.findField(SoundManager.class, SOUND_SYSTEM_MAPPING).get(soundManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SoundSystem getSoundSystem() {
		if (!loaded) {
			initialize();
			loaded = true;
		}
		return soundSystem;
	}

	private File getFile(String name) {
		return new File(LoreExpansion.audioFolder, name + ".ogg");
	}

	public void play(String name) {
		if (!nowPlaying.isEmpty()) {
			stop();
		}

		try {
			File file = getFile(name);
			URL url = file.toURI().toURL();

			nowPlaying = getSoundSystem().quickStream(true, url, file.getName(), false, 0F, 0F, 0F, 1, 0F);
			nameToTempMap.put(name, nowPlaying);
		} catch (Exception ex) {
			nowPlaying = "";
			ex.printStackTrace();
		}
	}

	public void stop() {
		if (nowPlaying.isEmpty()) {
			return;
		}

		getSoundSystem().stop(nowPlaying);
		nowPlaying = "";
	}

	public boolean isPlaying(String name) {
		return nameToTempMap.containsKey(name) && nameToTempMap.get(name).equals(nowPlaying);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		boolean currentState = false;
		if (Minecraft.getMinecraft().isGamePaused()) {
			currentState = true;
		}

		if (currentState && !paused) {
			getSoundSystem().pause(nowPlaying);
			paused = true;
		} else if (!currentState && paused) {
			getSoundSystem().play(nowPlaying);
			paused = false;
		}
	}
}
