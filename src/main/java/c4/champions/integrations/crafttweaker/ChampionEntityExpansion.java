package c4.champions.integrations.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenExpansion("crafttweaker.entity.IEntity")
public class ChampionEntityExpansion {

    @ZenMethod
    public static boolean isChampion(IEntity entity) {
        return ChampionCrTUtil.isChampion(entity);
    }

    @ZenMethod
    public static int getChampionTier(IEntity entity) {
        return ChampionCrTUtil.getTier(entity);
    }

    @ZenMethod
    public static String[] getChampionAffixes(IEntity entity) {
        return ChampionCrTUtil.getAffixes(entity);
    }

    @ZenMethod
    public static String getChampionName(IEntity entity) {
        return ChampionCrTUtil.getName(entity);
    }

    @ZenMethod
    public static boolean setChampionTier(IEntity entity, int tier) {
        return ChampionCrTUtil.setTier(entity, tier);
    }

    @ZenMethod
    public static boolean addChampionTier(IEntity entity, int tier) {
        return ChampionCrTUtil.addTier(entity, tier);
    }

    @ZenMethod
    public static boolean setChampionName(IEntity entity, String name) {
        return ChampionCrTUtil.setName(entity, name);
    }

    @ZenMethod
    public static boolean addChampionAffix(IEntity entity, String identifier) {
        return ChampionCrTUtil.addAffix(entity, identifier);
    }

    @ZenMethod
    public static boolean setChampionAffixes(IEntity entity, String[] identifiers) {
        return ChampionCrTUtil.setAffixes(entity, identifiers);
    }

    @ZenMethod
    public static boolean setChampion(IEntity entity, int tier, String[] identifiers) {
        return ChampionCrTUtil.setChampion(entity, tier, identifiers);
    }

    @ZenMethod
    public static boolean setChampion(IEntity entity, int tier, String[] identifiers, String name) {
        return ChampionCrTUtil.setChampion(entity, tier, identifiers, name);
    }

    @ZenMethod
    public static boolean setChampionRandomName(IEntity entity, int tier, String[] identifiers) {
        return ChampionCrTUtil.setChampionRandomName(entity, tier, identifiers);
    }
}
