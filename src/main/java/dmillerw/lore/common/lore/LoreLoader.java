package dmillerw.lore.common.lore;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.lore.data.Commands;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import dmillerw.lore.common.lore.data.LoreTags;
import dmillerw.lore.common.lore.data.json.CommandDeserializer;
import dmillerw.lore.common.misc.FileHelper;
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
        for (File file : LoreExpansion.loreFolder.listFiles()) {
            if (FileHelper.isJSONFile(file)) {
                try {
                    LoreLoader.INSTANCE.loadLore(file);
                } catch (Exception ex) {
                    LoreExpansion.logger.warn(String.format("Failed to parse %s", file.getName()));
                    ex.printStackTrace();
                }
            }
        }

        File tagFile = new File(LoreExpansion.configFolder + "/tags.json");
        if (tagFile.exists()) {
            LoreLoader.INSTANCE.loadLoreTags(tagFile);
        } else {
            try {
                LoreLoader.INSTANCE.saveDefaultLoreTags(tagFile);
            } catch (IOException ex) {
                LoreExpansion.logger.warn("Failed to save default tags.json. This isn't a huge issue.");
            }
        }
    }

    public static final LoreLoader INSTANCE = new LoreLoader();

    private static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Commands.class, new CommandDeserializer());
        gson = builder.create();
    }

    private Map<LoreKey, Lore> lore = Maps.newHashMap();

    private Map<Integer, String> dimensionNameCache = Maps.newHashMap();

    private LoreTags loreTags = new LoreTags();

    public int[] getAllDimensions() {
        List<Integer> list = new ArrayList<Integer>();
        for (Lore lore : getAllLore()) {
            if (!list.contains(lore.dimension)) {
                list.add(lore.dimension);
            }
        }
        return ArrayUtils.toPrimitive(list.toArray(new Integer[list.size()]));
    }

    public Lore[] getAllLore() {
        return lore.values().toArray(new Lore[lore.size()]);
    }

    public String getDimensionName(int dimension) {
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

    private String interpretDimensionName(int dimension) {
        switch (dimension) {
            case -1: return "Nether";
            case 0: return "Overworld";
            case 1: return "End";
            default: return getDimensionName(dimension);
        }
    }

    public String getLoreTag(int dimension) {
        String tag = loreTags.defaultTag;
        if (loreTags.mapping.containsKey(dimension)) {
            tag = loreTags.mapping.get(dimension);
        }
        return String.format(tag, interpretDimensionName(dimension));
    }

    public Lore getLore(int page, int dimension) {
        LoreKey key = new LoreKey(page, dimension);
        return getLore(key);
    }

    public Lore getLore(LoreKey key) {
        if (!lore.containsKey(key)) {
            return null;
        }
        return lore.get(key);
    }

    public void clear() {
        lore.clear();
        loreTags = new LoreTags();
    }

    public void loadLore(File file) throws Exception {
        Lore lore = gson.fromJson(new FileReader(file), Lore.class);
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

        this.lore.put(LoreKey.fromLore(lore), lore);
    }

    public void loadLoreTags(File file) {
        try {
            loreTags = gson.fromJson(new FileReader(file), LoreTags.class);
        } catch (IOException ex) {
            // LOG ERROR
        }
    }

    public void saveDefaultLoreTags(File file) throws IOException {
        LoreTags defaultTags = new LoreTags();
        String json = gson.toJson(defaultTags, LoreTags.class);
        FileWriter writer = new FileWriter(file);
        writer.append(json);
        writer.close();
    }
}
