package c4.champions.integrations.crafttweaker;

import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import c4.champions.common.champion.ChampionService;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

final class ChampionCrTUtil {

    private ChampionCrTUtil() {}

    static boolean isChampion(IEntity entity) {
        return getElite(entity) != null;
    }

    static int getTier(IEntity entity) {
        Champion chp = getElite(entity);
        return chp != null && chp.getRank() != null ? chp.getRank().getTier() : 0;
    }

    static String getName(IEntity entity) {
        Champion chp = getElite(entity);
        String name = chp != null ? chp.getName() : null;
        return name == null ? "" : name;
    }

    static String[] getAffixes(IEntity entity) {
        Champion chp = getElite(entity);
        return chp == null ? new String[]{} : chp.getAffixIds().toArray(new String[0]);
    }

    static boolean setTier(IEntity entity, int tier) {
        EntityLiving living = getLiving(entity);
        return living != null && ChampionService.setTier(living, tier);
    }

    static boolean addTier(IEntity entity, int addTier) {
        return setTier(entity, getTier(entity) + addTier);
    }

    static boolean setName(IEntity entity, String name) {
        EntityLiving living = getLiving(entity);
        return living != null && ChampionService.setName(living, name);
    }

    static boolean addAffix(IEntity entity, String identifier) {
        EntityLiving living = getLiving(entity);
        return living != null && ChampionService.addAffix(living, identifier);
    }

    static boolean setAffixes(IEntity entity, String[] identifiers) {
        EntityLiving living = getLiving(entity);
        return living != null && ChampionService.setAffixes(living, identifiers == null ? null :
                Arrays.asList(identifiers));
    }

    static boolean setChampion(IEntity entity, int tier, String[] affixes) {
        return setChampion(entity, tier, affixes, null, false);
    }

    static boolean setChampion(IEntity entity, int tier, String[] affixes, String name) {
        return setChampion(entity, tier, affixes, name, false);
    }

    static boolean setChampionRandomName(IEntity entity, int tier, String[] affixes) {
        return setChampion(entity, tier, affixes, null, true);
    }

    private static boolean setChampion(IEntity entity, int tier, String[] identifiers, @Nullable String name,
                                       boolean randomName) {
        EntityLiving living = getLiving(entity);
        return living != null && ChampionService.set(living, tier, identifiers == null ? null :
                Arrays.asList(identifiers), name, randomName);
    }

    @Nullable
    private static Champion getElite(IEntity entity) {
        return getElite(getLiving(entity));
    }

    @Nullable
    private static Champion getElite(@Nullable EntityLiving living) {
        return living == null ? null : ChampionCapability.getElite(living);
    }

    @Nullable
    private static EntityLiving getLiving(IEntity entity) {
        Entity mcEntity = CraftTweakerMC.getEntity(entity);
        return mcEntity instanceof EntityLiving ? (EntityLiving)mcEntity : null;
    }

}
