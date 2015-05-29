package dmillerw.lore.common.lore.data;

import com.google.gson.annotations.SerializedName;

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

    @SerializedName("sorting_index")
    public int sortingIndex = 0;

    public Commands commands = Commands.BLANK;

    public boolean autoplay = true;
    public boolean notify = true;

    public boolean hasSound() {
        return !sound.isEmpty();
    }
}
