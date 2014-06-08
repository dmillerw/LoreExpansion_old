package dmillerw.lore.misc;

/**
 * @author dmillerw
 */
public class StringHelper {

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

}
