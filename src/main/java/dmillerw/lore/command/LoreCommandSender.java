package dmillerw.lore.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class LoreCommandSender implements ICommandSender {

	public EntityPlayer player;

	public LoreCommandSender(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public String getCommandSenderName() {
		return player.getCommandSenderName();
	}

	@Override
	public IChatComponent func_145748_c_() {
		return player.func_145748_c_();
	}

	@Override
	public void addChatMessage(IChatComponent var1) {
		if (getEntityWorld().getWorldInfo().getGameRulesInstance().getGameRuleBooleanValue("commandBlockOutput")) {
			player.addChatMessage(var1);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return true;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return player.getPlayerCoordinates();
	}

	@Override
	public World getEntityWorld() {
		return player.getEntityWorld();
	}
}
