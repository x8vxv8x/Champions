package c4.champions.integrations.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.champions.ChampionsStage")
public class ChampionsCrT {

    @ZenMethod
    public static void addStage(String stage, String entity) {
        CraftTweakerAPI.apply(new ActionAddChampionStage(stage, entity));
    }

    @ZenMethod
    public static void addStage(String stage, String entity, int dimension) {
        CraftTweakerAPI.apply(new ActionAddChampionStage(stage, entity, dimension));
    }

    @ZenMethod
    public static void addTierStage(String stage, int tier) {
        CraftTweakerAPI.apply(new ActionAddTierStage(stage, tier));
    }

    @ZenMethod
    public static void addTierStage(String stage, int tier, int dimension) {
        CraftTweakerAPI.apply(new ActionAddTierStage(stage, tier, dimension));
    }
}
