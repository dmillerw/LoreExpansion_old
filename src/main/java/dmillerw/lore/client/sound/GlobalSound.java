package dmillerw.lore.client.sound;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

/**
 * @author dmillerw
 */
public class GlobalSound extends PositionedSound {

	protected GlobalSound(ResourceLocation resource, float volume, float pitch) {
		super(resource);

		this.volume = volume;
		this.field_147663_c = pitch;
		this.xPosF = 0F;
		this.yPosF = 0F;
		this.zPosF = 0F;
		this.repeat = false;
		this.field_147665_h = 0;
		this.field_147666_i = AttenuationType.NONE;
	}

}
