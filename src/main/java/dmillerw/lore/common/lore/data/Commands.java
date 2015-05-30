package dmillerw.lore.common.lore.data;

public class Commands {

    public static final Commands BLANK = new Commands();

    public CommandEntry[] commands;

    public static class CommandEntry {

        public String[] commands = new String[0];
        public int delay = 0;

        public CommandEntry() {

        }

        public CommandEntry(String[] commands, int delay) {
            this.commands = commands;
            this.delay = delay;
        }
    }
}
