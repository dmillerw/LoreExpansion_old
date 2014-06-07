package dmillerw.lore.core.handler;

import dmillerw.lore.LoreExpansion;
import dmillerw.lore.misc.FileHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author dmillerw
 */
public class DefaultFileHandler {

	public static final String[] FILES = new String[] {
		"lore/tutorial.json",
		"lore/audio/tutorial.ogg"
	};

	public static void initialize() {
		int fileCount = 0;
		for (File file : LoreExpansion.loreFolder.listFiles()) {
			if (FileHelper.isJSONFile(file)) fileCount++;
		}

		// User hasn't added files, and we haven't copied before, so copy
		if (fileCount == 0) {
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

}
