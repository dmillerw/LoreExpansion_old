package dmillerw.lore.common.lore.data;

/**
 * @author dmillerw
 */
public class Lore {

    public int page;
    public int dimension = Integer.MAX_VALUE; // Default

    public String title = "";
    public String body = "";
    public String sound = "";

    public Commands commands = Commands.BLANK;

    public boolean autoplay = true;
    public boolean notify = true;

    public boolean hasSound() {
        return !sound.isEmpty();
    }

    public boolean validDimension(int dimension) {
        return this.dimension == dimension;
    }
}
