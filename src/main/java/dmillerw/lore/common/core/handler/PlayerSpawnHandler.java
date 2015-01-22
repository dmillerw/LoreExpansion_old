package dmillerw.lore.common.core.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.lore.common.lore.PlayerHandler;

public class PlayerSpawnHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			if (event.entity instanceof EntityPlayer) {
				PlayerHandler.attach((EntityPlayer) event.entity);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		// When the player dies and is respawned, NBT data associated with its old entity is lost.
		// This will copy the data from the old entity to the new one.
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			NBTTagCompound temp = new NBTTagCompound();
			PlayerHandler.getCollectedLore( event.original ).saveNBTData(temp);
			PlayerHandler.attach(event.entityPlayer);
			PlayerHandler.getCollectedLore(event.entityPlayer).loadNBTData(temp);
		}
	}

}
