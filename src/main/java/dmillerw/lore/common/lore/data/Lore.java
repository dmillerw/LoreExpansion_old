package dmillerw.lore.common.lore.data;

/**
 * @author dmillerw
 */
public class Lore {

    public static final String GLOBAL = "GLOBAL";

    public String category = GLOBAL;
    public String ident = "";
    public String title = "";
    public String body = "";
    public String sound = "";

    public Commands commands = Commands.BLANK;

    public boolean autoplay = true;
    public boolean notify = true;

    public boolean hasSound() {
        return !sound.isEmpty();
    }
}
