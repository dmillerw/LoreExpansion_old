package dmillerw.lore.common.command;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class LoreCommandSender extends CommandBlockLogic {

	public EntityPlayer player;

	public LoreCommandSender(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public String getCommandSenderName() {
		return "Lore";
	}

	@Override
	public IChatComponent func_145748_c_() {
		return null;
	}

	@Override
	public void addChatMessage(IChatComponent var1) {

	}

	@Override
	public void func_145756_e() {

	}

	@Override
	public int func_145751_f() {
		return 0;
	}

	@Override
	public void func_145757_a(ByteBuf var1) {

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
