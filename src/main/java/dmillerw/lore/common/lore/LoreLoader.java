package dmillerw.lore.common.lore;

import com.google.common.collect.Maps;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.lib.ExtensionFilter;
import dmillerw.lore.common.lib.JsonUtil;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.LoreTags;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class LoreLoader {

    public static void initialize() {
        for (File file : LoreExpansion.loreFolder.listFiles(ExtensionFilter.JSON)) {
            try {
                LoreLoader.loadLore(file);
            } catch (Exception ex) {
                LoreExpansion.logger.warn(String.format("Failed to parse %s", file.getName()));
                ex.printStackTrace();
            }
        }

        File tagFile = new File(LoreExpansion.configFolder + "/tags.json");
        if (tagFile.exists()) {
            LoreLoader.loadLoreTags(tagFile);
        } else {
            try {
                LoreLoader.saveDefaultLoreTags(tagFile);
            } catch (IOException ex) {
                LoreExpansion.logger.warn("Failed to save default tags.json. This isn't a huge issue.");
            }
        }
    }

    private static Map<LoreKey, Lore> loreMap = Maps.newHashMap();
    private static Map<Integer, String> dimensionNameCache = Maps.newHashMap();

    private static LoreTags loreTags = new LoreTags();

    public static int[] getAllDimensions() {
        List<Integer> list = new ArrayList<Integer>();
        for (Lore lore : getAllLore()) {
            if (!list.contains(lore.dimension)) {
                list.add(lore.dimension);
            }
        }
        return ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()]));
    }

    public static Lore[] getAllLore() {
        return loreMap.values().toArray(new Lore[loreMap.size()]);
    }

    private static String getDimensionName(int dimension) {
        try {
            if (dimensionNameCache.containsKey(dimension)) {
                return dimensionNameCache.get(dimension);
            } else {
                WorldProvider worldProvider = DimensionManager.createProviderFor(dimension);
                String name = "";
                if (worldProvider != null) {
                    name = worldProvider.getDimensionName();
                }
                dimensionNameCache.put(dimension, name);
                return name;
            }
        } catch (RuntimeException ex) {
            // Plz don't crash :P
            dimensionNameCache.put(dimension, "");
            return "";
        }
    }

    private static String interpretDimensionName(int dimension) {
        switch (dimension) {
            case -1:
                return "Nether";
            case 0:
                return "Overworld";
            case 1:
                return "End";
            default:
                return getDimensionName(dimension);
        }
    }

    public static String getLoreTag(int dimension) {
        String tag = loreTags.defaultTag;
        if (loreTags.mapping.containsKey(dimension)) {
            tag = loreTags.mapping.get(dimension);
        }
        return String.format(tag, interpretDimensionName(dimension));
    }

    public static Lore getLore(int page, int dimension) {
        LoreKey key = new LoreKey(page, dimension);
        return getLore(key);
    }

    public static Lore getLore(LoreKey key) {
        if (!loreMap.containsKey(key)) {
            return null;
        }
        return loreMap.get(key);
    }

    public static void clear() {
        loreMap.clear();
        loreTags = new LoreTags();
    }

    public static void loadLore(File file) throws Exception {
        Lore lore = JsonUtil.gson().fromJson(new FileReader(file), Lore.class);
        // Make sure page is positive and above 0
        if (lore.page <= 0) {
            LoreExpansion.logger.warn(String.format("Page number in %s must be above 0. Setting to 1", file.getName()));
            lore.page = 1;
        }
        // Check to see if file exists
        if (!lore.sound.isEmpty()) {
            File audio = new File(LoreExpansion.audioFolder, lore.sound + ".ogg");
            if (!audio.exists() || !audio.isFile()) {
                LoreExpansion.logger.warn(String.format("Could not find %s audio file as defined in %s", lore.sound, file.getName()));
                lore.sound = "";
            }
        }

        loreMap.put(LoreKey.fromLore(lore), lore);
    }

    public static void loadLoreTags(File file) {
        try {
            loreTags = JsonUtil.gson().fromJson(new FileReader(file), LoreTags.class);
        } catch (IOException ex) {
            // LOG ERROR
        }
    }

    public static void saveDefaultLoreTags(File file) throws IOException {
        LoreTags defaultTags = new LoreTags();
        String json = JsonUtil.gson().toJson(defaultTags, LoreTags.class);
        FileWriter writer = new FileWriter(file);
        writer.append(json);
        writer.close();
    }
}
