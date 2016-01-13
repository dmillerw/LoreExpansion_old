package dmillerw.lore.common.lore;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import dmillerw.lore.LoreExpansion;
import dmillerw.lore.common.lib.ExtensionFilter;
import dmillerw.lore.common.lib.JsonUtil;
import dmillerw.lore.common.lore.data.Lore;
import dmillerw.lore.common.lore.data.LoreKey;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author dmillerw
 */
public class LoreLoader {

    public static void initialize() {
        // Forces GLOBAL to be the first category
        categories.add(Lore.GLOBAL);

        for (File file : LoreExpansion.loreFolder.listFiles(ExtensionFilter.JSON)) {
            try {
                LoreLoader.loadLore(file);
            } catch (Exception ex) {
                LoreExpansion.logger.warn(String.format("Failed to parse %s", file.getName()));
                ex.printStackTrace();
            }
        }

        // Sort
        for (String category : getAllCategories()) {
            LinkedList<Lore> loreList = Lists.newLinkedList();
            loreList.addAll(getMap(category).values());
            Collections.sort(loreList, new Comparator<Lore>() {
                @Override
                public int compare(Lore o1, Lore o2) {
                    return Integer.compare(o1.sortingIndex, o2.sortingIndex);
                }
            });

            LinkedHashMap<String, Lore> newMap = Maps.newLinkedHashMap();

            for (Lore lore :loreList) {
                newMap.put(lore.ident, lore);
            }

            loreMap.put(category, newMap);
        }

        Collections.sort(categories);
    }

    public static void loadLore(File file) throws Exception {
        Lore lore = JsonUtil.gson().fromJson(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"), Lore.class);

        if (!categories.contains(lore.category))
            categories.add(lore.category);

        lore.ident = FilenameUtils.getBaseName(file.getName());

        // Check to see if file exists
        if (!lore.sound.isEmpty()) {
            File audio = new File(LoreExpansion.audioFolder, lore.sound + ".ogg");
            if (!audio.exists() || !audio.isFile()) {
                LoreExpansion.logger.warn(String.format("Could not find %s audio file as defined in %s", lore.sound, file.getName()));
                lore.sound = "";
            }
        }

        LinkedHashMap<String, Lore> submap = getMap(lore.category);
        submap.put(lore.ident, lore);
        loreMap.put(lore.category, submap);
    }

    private static LinkedList<String> categories = Lists.newLinkedList();
    private static Map<String, LinkedHashMap<String, Lore>> loreMap = Maps.newHashMap();

    private static LinkedHashMap<String, Lore> getMap(String category) {
        LinkedHashMap<String, Lore> map = loreMap.get(category);
        if (map == null) {
            map = Maps.newLinkedHashMap();
            loreMap.put(category, map);
        }
        return map;
    }

    public static List<String> getAllCategories() {
        return categories;
    }

    public static ImmutableSet<Lore> getAllLore() {
        ImmutableSet.Builder<Lore> builder = new ImmutableSet.Builder<Lore>();
        for (LinkedHashMap<String, Lore> category : loreMap.values()) {
            builder.addAll(category.values());
        }
        return builder.build();
    }

    public static ImmutableSet<Lore> getAllLoreForCategory(String category) {
        ImmutableSet.Builder<Lore> builder = new ImmutableSet.Builder<Lore>();
        builder.addAll(getMap(category).values());
        return builder.build();
    }

    public static Lore getLore(LoreKey key) {
        return getMap(key.category).get(key.ident);
    }

    public static Lore getLore(String category, String ident) {
        return getMap(category).get(ident);
    }

    public static void clear() {
        loreMap.clear();
    }
}
