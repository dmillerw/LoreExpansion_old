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

	/** File-name to be read */
	private final String file;

	public SoundLoader(String file) {
		this.file = file;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent event) {
		Map resourceManagers = ReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager(), 2);
		FallbackResourceManager resourceManager = (FallbackResourceManager) resourceManagers.get(PREFIX);
		resourceManager.addResourcePack(new LoreResourcePack(file));

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
	public ISound play() {
		ResourceLocation location = new ResourceLocation(PREFIX, file);
		ISound sound = new GlobalSound(location, 4F, 1F);
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		return sound;
	}
}
