package dmillerw.lore.common.core.handler;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.lore.common.command.LoreCommandSender;
import dmillerw.lore.common.lore.data.Commands;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

public class CommandDelayHandler {

    private static class DelayedCommand {

        public String[] commands;
        public int timer;

        private WeakReference<EntityPlayer> player;

        public void setPlayer(EntityPlayer entityPlayer) {
            this.player = new WeakReference<EntityPlayer>(entityPlayer);
        }

        public EntityPlayer getPlayer() {
            return player.get();
        }
    }

    private static Set<DelayedCommand> delayedCommands = Sets.newHashSet();

    public static void queueCommand(EntityPlayer entityPlayer, Commands.CommandEntry commandEntry) {
        DelayedCommand delayedCommand = new DelayedCommand();
        delayedCommand.commands = commandEntry.commands;
        delayedCommand.timer = commandEntry.delay;
        delayedCommand.setPlayer(entityPlayer);
        delayedCommands.add(delayedCommand);
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        Iterator<DelayedCommand> delayedIterator = delayedCommands.iterator();
        while (delayedIterator.hasNext()) {
            DelayedCommand delayedCommand = delayedIterator.next();

            delayedCommand.timer--;
            if (delayedCommand.timer <= 0) {
                CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
                LoreCommandSender commandSender = new LoreCommandSender(delayedCommand.getPlayer());

                for (String command : delayedCommand.commands) {
                    ch.executeCommand(commandSender, command);
                }

                delayedIterator.remove();
            }
        }
    }
}
