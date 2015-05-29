package dmillerw.lore.common.core.handler;

import dmillerw.lore.LoreExpansion;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author dmillerw
 */
public class DefaultFileHandler {

    public static final String[] FILES = new String[]{
            "lore/tutorial.json",
            "lore/audio/tutorial.ogg"
    };

    public static void saveDefaults() {
        for (String str : FILES) {
            URL input = LoreExpansion.class.getResource("/assets/loreexp/defaults/" + str);
            File output = new File(LoreExpansion.configFolder, str);

            if (input != null) {
                try {
                    FileUtils.copyURLToFile(input, output);
                } catch (IOException ex) {
                    LoreExpansion.logger.warn("Failed to copy " + str + " to " + output.getAbsoluteFile());
                }
            }
        }
    }
}
