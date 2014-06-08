package dmillerw.lore.lore.data;

/**
 * @author dmillerw
 */
public class Lore {

	public int page;
	public int dimension = Integer.MAX_VALUE; // Default
	public String title = "";
	public String lore = "";
	public String sound = "";
	public String[] commands = new String[0];
	public boolean autoplay = false;

	public boolean hasSound() {
		return !sound.isEmpty();
	}

	public boolean validDimension(int dimension) {
		return this.dimension == dimension;
	}
}
