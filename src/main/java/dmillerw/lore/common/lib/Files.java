package dmillerw.lore.common.lib;

import java.io.File;

public class Files {

    public static File mkdir(File file, String string) {
        File ret = new File(file, string);
        if (!ret.exists())
            ret.mkdir();
        return ret;
    }

    public static File mkdir(String string) {
        File ret = new File(string);
        if (!ret.exists())
            ret.mkdir();
        return ret;
    }
}
