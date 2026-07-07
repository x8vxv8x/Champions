package c4.champions;

import c4.champions.command.CommandChampionEgg;
import c4.champions.command.CommandSpawnChampion;
import c4.champions.command.CommandSpawnChampionAt;
import c4.champions.common.EventHandlerCommon;
import c4.champions.common.affix.AffixEvents;
import c4.champions.common.affix.Affixes;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.loot.EntityIsChampion;
import c4.champions.common.rank.RankManager;
import c4.champions.common.champion.ChampionService;
import c4.champions.integrations.scalinghealth.ChampionDifficulty;
import c4.champions.network.NetworkHandler;
import c4.champions.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(   modid = Champions.MODID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION)
public class Champions
{
    public static final String MODID = "champions";

    public static boolean isGameStagesLoaded = false;

    public static Logger logger;

    @SidedProxy(clientSide = "c4.champions.proxy.ClientProxy",
                serverSide = "c4.champions.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        LootTableList.register(new ResourceLocation(Champions.MODID, "champion_loot"));
        EntityPropertyManager.registerProperty(new EntityIsChampion.Serializer());
        proxy.preInit(evt);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        NetworkHandler.register();
        ChampionCapability.register();
        Affixes.registerAffixes();
        MinecraftForge.EVENT_BUS.register(new AffixEvents());
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
        proxy.init(evt);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

        if (Loader.isModLoaded("gamestages")) {
            isGameStagesLoaded = true;
        }

        if (Loader.isModLoaded("scalinghealth")) {
            ChampionDifficulty.loadConfigs();
        }
        RankManager.readRanksFromJson();
        AffixFilterManager.readAffixFiltersFromJson();
        ChampionService.parseConfigs();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandSpawnChampion());
        evt.registerServerCommand(new CommandChampionEgg());
        evt.registerServerCommand(new CommandSpawnChampionAt());
    }
}
