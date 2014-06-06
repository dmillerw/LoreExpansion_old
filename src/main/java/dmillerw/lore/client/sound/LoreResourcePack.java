package dmillerw.lore.client.sound;

import com.google.common.collect.Sets;
import dmillerw.lore.LoreExpansion;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.Set;

/**
 * @author dmillerw
 */
public class LoreResourcePack extends AbstractResourcePack {

	private static final Set<String> DOMAIN = Sets.newHashSet("loreexp");

	private final String name;

	public LoreResourcePack(String name) {
		super(LoreExpansion.audioFolder);

		this.name = name;
	}

	@Override
	protected InputStream getInputStreamByName(String name) throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, this.name + ".ogg")));
	}

	@Override
	protected boolean hasResourceName(String name) {
		return name.contains(this.name) && name.endsWith(".ogg") && (new File(resourcePackFile, this.name + ".ogg")).isFile();
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws FileNotFoundException {
		return getInputStreamByName(location.getResourcePath().replace("sounds/", ""));
	}

	@Override
	public boolean resourceExists(ResourceLocation location) {
		return hasResourceName(location.getResourcePath().replace("sounds/", ""));
	}

	@Override
	public Set getResourceDomains() {
		return DOMAIN;
	}
}
