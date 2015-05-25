package dmillerw.lore.common.lib;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author dmillerw
 */
public class ExtensionFilter implements FilenameFilter {

    public static final ExtensionFilter JSON = new ExtensionFilter("json");

    private final String extension;

    public ExtensionFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name) {
        return FilenameUtils.getExtension(name).equalsIgnoreCase(extension);
    }
}
