package c4.champions.integrations.crafttweaker;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffix;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

final class ChampionCrTUtil {

    private ChampionCrTUtil() {}

    static boolean isChampion(IEntity entity) {
        IChampionship chp = getChampionship(entity);
        return chp != null && ChampionHelper.isElite(chp.getRank());
    }

    static int getTier(IEntity entity) {
        IChampionship chp = getChampionship(entity);
        return chp != null && chp.getRank() != null ? chp.getRank().getTier() : 0;
    }

    static String getName(IEntity entity) {
        IChampionship chp = getChampionship(entity);
        String name = chp != null ? chp.getName() : null;
        return name == null ? "" : name;
    }

    static String[] getAffixes(IEntity entity) {
        IChampionship chp = getChampionship(entity);
        return chp == null ? new String[]{} : chp.getAffixes().toArray(new String[0]);
    }

    static boolean setTier(IEntity entity, int tier) {
        EntityLiving living = getLiving(entity);
        IChampionship chp = getChampionship(living);

        if (living == null || chp == null) {
            return false;
        }

        Rank rank = getRank(tier);

        if (rank == null) {
            return false;
        }
        boolean wasChampion = ChampionHelper.isElite(chp.getRank());
        chp.setRank(rank);

        if (rank.getTier() <= 0) {
            chp.setAffixes(Sets.newHashSet());
            chp.setName("");
        } else if (chp.getName() == null || chp.getName().isEmpty()) {
            chp.setName(ChampionHelper.generateRandomName());
        }

        if (!wasChampion && rank.getTier() > 0) {
            rank.applyGrowth(living);
        }
        sync(living, chp);
        return true;
    }

    static boolean addTier(IEntity entity, int addTier) {
        return setTier(entity, getTier(entity) + addTier);
    }

    static boolean setName(IEntity entity, String name) {
        EntityLiving living = getLiving(entity);
        IChampionship chp = getChampionship(living);

        if (living == null || chp == null) {
            return false;
        }
        chp.setName(name);
        sync(living, chp);
        return true;
    }

    static boolean addAffix(IEntity entity, String identifier) {
        EntityLiving living = getLiving(entity);
        IChampionship chp = getChampionship(living);
        AffixBase affix = AffixRegistry.getAffix(identifier);

        if (living == null || chp == null || affix == null || !ChampionHelper.isElite(chp.getRank())) {
            return false;
        }

        Map<String, NBTTagCompound> data = Maps.newHashMap(chp.getAffixData());

        if (data.containsKey(identifier)) {
            return true;
        }
        data.put(identifier, new NBTTagCompound());
        chp.setAffixData(data);
        affix.onInitialSpawn(living, chp);
        sync(living, chp);
        return true;
    }

    static boolean setAffixes(IEntity entity, String[] identifiers) {
        EntityLiving living = getLiving(entity);
        IChampionship chp = getChampionship(living);

        if (living == null || chp == null || !ChampionHelper.isElite(chp.getRank())) {
            return false;
        }
        Set<String> affixes = getValidAffixes(identifiers);

        if (affixes == null) {
            return false;
        }
        setAffixes(living, chp, affixes);
        sync(living, chp);
        return true;
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
        IChampionship chp = getChampionship(living);

        if (living == null || chp == null) {
            return false;
        }
        Rank rank = getRank(tier);
        Set<String> affixes = getValidAffixes(identifiers);

        if (rank == null || affixes == null || rank.getTier() <= 0) {
            return false;
        }
        boolean wasChampion = ChampionHelper.isElite(chp.getRank());
        chp.setRank(rank);
        setAffixes(living, chp, affixes);
        chp.setName(randomName || name == null || name.isEmpty() ? ChampionHelper.generateRandomName() : name);

        if (!wasChampion) {
            rank.applyGrowth(living);
        }
        sync(living, chp);
        return true;
    }

    @Nullable
    private static Rank getRank(int tier) {
        Rank rank = RankManager.getRankForTier(tier);
        return tier <= 0 || rank.getTier() > 0 ? rank : null;
    }

    @Nullable
    private static Set<String> getValidAffixes(String[] identifiers) {
        Set<String> affixes = Sets.newHashSet();

        if (identifiers == null) {
            return affixes;
        }

        for (String identifier : identifiers) {

            if (AffixRegistry.getAffix(identifier) == null) {
                return null;
            }
            affixes.add(identifier);
        }
        return affixes;
    }

    private static void setAffixes(EntityLiving living, IChampionship chp, Set<String> identifiers) {
        Set<String> oldAffixes = chp.getAffixes();
        Map<String, NBTTagCompound> oldData = chp.getAffixData();
        Map<String, NBTTagCompound> newData = Maps.newHashMap();

        for (String identifier : identifiers) {
            NBTTagCompound data = oldData.get(identifier);
            newData.put(identifier, data == null ? new NBTTagCompound() : data);
        }
        chp.setAffixData(newData);

        for (String identifier : identifiers) {

            if (!oldAffixes.contains(identifier)) {
                AffixBase affix = AffixRegistry.getAffix(identifier);

                if (affix != null) {
                    affix.onInitialSpawn(living, chp);
                }
            }
        }
    }

    @Nullable
    private static IChampionship getChampionship(IEntity entity) {
        return getChampionship(getLiving(entity));
    }

    @Nullable
    private static IChampionship getChampionship(@Nullable EntityLiving living) {
        return living == null ? null : CapabilityChampionship.getChampionship(living);
    }

    @Nullable
    private static EntityLiving getLiving(IEntity entity) {
        Entity mcEntity = CraftTweakerMC.getEntity(entity);
        return mcEntity instanceof EntityLiving ? (EntityLiving)mcEntity : null;
    }

    private static void sync(EntityLiving living, IChampionship chp) {

        if (living.world instanceof WorldServer && chp.getRank() != null) {
            WorldServer world = (WorldServer)living.world;
            PacketSyncAffix packet = new PacketSyncAffix(living.getEntityId(), chp.getRank().getTier(),
                    chp.getAffixData(), getSafeName(chp));

            for (EntityPlayer player : world.getEntityTracker().getTrackingPlayers(living)) {

                if (player instanceof EntityPlayerMP) {
                    NetworkHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)player);
                }
            }
        }
    }

    private static String getSafeName(IChampionship chp) {
        String name = chp.getName();
        return name == null ? "" : name;
    }
}
