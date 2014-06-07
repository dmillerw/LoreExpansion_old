package dmillerw.lore.client.sound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author dmillerw
 */
public class SoundLoader {

	public static final String PREFIX = "loreexp";

	public static void preload(SoundLoader loader) {
		loader.start();
		loader.stop();
	}

	public static ISound lastSound;

	/** File-name to be read */
	private final String file;

	private boolean loaded = false;

	public SoundLoader(String file) {
		this.file = file;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent event) {
		loaded = false;

		Map resourceManagers = ReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager(), 2);
		FallbackResourceManager resourceManager = (FallbackResourceManager) resourceManagers.get(PREFIX);
		resourceManager.addResourcePack(new LoreResourcePack(file));
	}

	@SideOnly(Side.CLIENT)
	public void registerSound() {
		SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

		SoundList list = new SoundList();
		list.setSoundCategory(SoundCategory.MASTER);

		SoundList.SoundEntry entry = new SoundList.SoundEntry();
		entry.setSoundEntryName(file);
		list.getSoundList().add(entry);

		Method method = ReflectionHelper.findMethod(SoundHandler.class, soundHandler, new String[] {"loadSoundResource", "func_147693_a", "a"}, new Class[] {ResourceLocation.class, SoundList.class});

		try {
			method.invoke(soundHandler, new ResourceLocation(PREFIX, file), list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public ISound getSound() {
		if (!loaded) {
			registerSound();
			loaded = true;
		}

		if (lastSound == null) {
			ResourceLocation location = new ResourceLocation(PREFIX, file);
			lastSound = new GlobalSound(location, 4F, 1F);
		}

		return lastSound;
	}

	@SideOnly(Side.CLIENT)
	public void start() {
		Minecraft.getMinecraft().getSoundHandler().playSound(getSound());
	}

	@SideOnly(Side.CLIENT)
	public boolean isPlaying() {
		boolean playing = lastSound != null && lastSound.getPositionedSoundLocation() == getSound().getPositionedSoundLocation() && Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(getSound());
		if (!playing) {
			lastSound = null;
		}
		return playing;
	}

	@SideOnly(Side.CLIENT)
	public void stop() {
		if (isPlaying()) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(getSound());
			lastSound = null;
		}
	}
}
