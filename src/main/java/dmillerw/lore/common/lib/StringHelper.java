package dmillerw.lore.common.lib;

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

    public static String indent(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String pretty(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean toUpper = true;
        for (char c : string.toCharArray()) {
            if (toUpper) {
                stringBuilder.append(Character.toUpperCase(c));
                toUpper = false;
            } else if (Character.isDigit(c)) {
                stringBuilder.append(' ').append(c);
            } else if ((c == '-' || c == '_') || Character.isWhitespace(c)) {
                stringBuilder.append(' ');
                toUpper = true;
            } else {
                stringBuilder.append(Character.toLowerCase(c));
            }

        }

        return stringBuilder.toString();
    }
}
