package dmillerw.lore.common.lib;

/**
 * @author dmillerw
 */
public class Pair<L, R> {

    public static <L, R> Pair<L, R> of(L l, R r) {
        return new Pair<L, R>(l, r);
    }

    public final L left;

    public final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
