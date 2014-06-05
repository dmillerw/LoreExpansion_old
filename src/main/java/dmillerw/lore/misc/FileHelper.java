package dmillerw.lore.misc;

import java.io.File;

/**
 * @author dmillerw
 */
public class FileHelper {

	public static boolean isJSONFile(File file) {
		String name = file.getName();
		return (name.substring(name.lastIndexOf(".") + 1, name.length()).equalsIgnoreCase("json"));
	}

}
