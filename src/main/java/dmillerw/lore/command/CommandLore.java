package dmillerw.lore.command;

import dmillerw.lore.lore.LoreLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * @author dmillerw
 */
public class CommandLore extends CommandBase {

	@Override
	public String getCommandName() {
		return "lore";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/lore reload";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length != 1) {
			throw new WrongUsageException(getCommandUsage(sender));
		}

		if (args[0].equalsIgnoreCase("reload")) {
			LoreLoader.INSTANCE.clear();
			LoreLoader.initialize();
		}
	}
}
