package dmillerw.lore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.lore.core.GuiHandler;
import dmillerw.lore.core.proxy.CommonProxy;
import dmillerw.lore.item.ItemJournal;
import dmillerw.lore.item.ItemLoreScrap;
import dmillerw.lore.lore.LoreLoader;
import dmillerw.lore.misc.FileHelper;
import dmillerw.lore.network.NetworkEventHandler;
import dmillerw.lore.network.PacketHandler;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * @author dmillerw
 */
@Mod(modid = "LoreExp", name = "Lore Expansion", version = "1.0.0", dependencies = "required-after:Forge@[10.12.1.1112,)")
public class LoreExpansion {

	private static final String CONFIG_FOLDER = "LoreExpansion";
	private static final String LORE_FOLDER = "lore";

	public static final Logger logger = LogManager.getLogger("Lore Expansion");

	@Mod.Instance("LoreExp")
	public static LoreExpansion instance;

	@SidedProxy(serverSide = "dmillerw.lore.core.proxy.CommonProxy", clientSide = "dmillerw.lore.core.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static Item loreScrap;
	public static Item journal;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		loreScrap = new ItemLoreScrap().setUnlocalizedName("scrap");
		GameRegistry.registerItem(loreScrap, "scrap");

		journal = new ItemJournal().setUnlocalizedName("journal");
		GameRegistry.registerItem(journal, "journal");

		File loreFolder = new File(event.getModConfigurationDirectory(), CONFIG_FOLDER + "/" + LORE_FOLDER);
		if (!loreFolder.exists()) {
			loreFolder.mkdirs();
		}

		for (File file : loreFolder.listFiles()) {
			if (FileHelper.isJSONFile(file)) {
				try {
					LoreLoader.INSTANCE.loadLore(file);
				} catch (Exception ex) {
					logger.warn(String.format("Failed to parse %s", file.getName()));
					ex.printStackTrace();
				}
			}
		}

		PacketHandler.init();

		FMLCommonHandler.instance().bus().register(new NetworkEventHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

}
