package dmillerw.lore.common.lore.data;

public class Commands {

    public static final Commands BLANK = new Commands();

    public CommandEntry[] commands;

    public static class CommandEntry {

        public String command = "";
        public int delay = 0;

        public CommandEntry() {

        }

        public CommandEntry(String command, int delay) {
            this.command = command;
            this.delay = delay;
        }
    }
}
