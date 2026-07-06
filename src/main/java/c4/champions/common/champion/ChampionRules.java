package c4.champions.common.champion;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

public final class ChampionRules {

    private static final Field IS_COMPLETE = ReflectionHelper.findField(TileEntityBeacon.class,
            "isComplete", "field_146015_k");

    private static final Set<Integer> dimensions = Sets.newHashSet();
    private static final Set<ResourceLocation> mobs = Sets.newHashSet();
    private static final Map<ResourceLocation, Tuple<Integer, Integer>> champions = Maps.newHashMap();

    private ChampionRules() {
    }

    public static boolean isChampionEntity(Entity entity) {
        if (ConfigHandler.includeNeutral) {
            return entity instanceof EntityLiving && isValidEntity(entity);
        }
        return entity instanceof EntityLiving && entity instanceof IMob && isValidEntity(entity);
    }

    public static boolean isElite(Champion champion) {
        return champion != null && isElite(champion.getRank());
    }

    public static boolean isElite(c4.champions.common.rank.Rank rank) {
        return rank != null && rank.getTier() > 0;
    }

    public static boolean isValidDimension(int dim) {
        if (dimensions.isEmpty()) {
            return true;
        }
        return (ConfigHandler.dimensionPermission == ConfigHandler.PermissionMode.BLACKLIST) != dimensions.contains(dim);
    }

    public static boolean isValidEntity(Entity entity) {
        ResourceLocation id = EntityList.getKey(entity);

        if (id == null) {
            return false;
        }
        if (mobs.isEmpty()) {
            return true;
        }
        return (ConfigHandler.mobPermission == ConfigHandler.PermissionMode.BLACKLIST) != mobs.contains(id);
    }

    @Nullable
    static Tuple<Integer, Integer> curatedTier(EntityLiving entity) {
        return champions.get(EntityList.getKey(entity));
    }

    static boolean nearActiveBeacon(EntityLiving entity) {
        int range = ConfigHandler.beaconRange;

        if (range <= 0) {
            return false;
        }

        for (TileEntity te : entity.world.tickableTileEntities) {
            BlockPos pos = te.getPos();

            if (Math.sqrt(entity.getDistanceSq(pos)) <= range && te instanceof TileEntityBeacon) {
                try {
                    if (IS_COMPLETE.getBoolean(te)) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    Champions.logger.log(Level.ERROR, "Error reading isComplete from beacon!");
                }
            }
        }
        return false;
    }

    static void parseConfig() {
        dimensions.clear();
        mobs.clear();
        champions.clear();

        for (String value : ConfigHandler.dimensionList) {
            try {
                dimensions.add(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Non-integer found in dimension config! " + value);
            }
        }

        for (String value : ConfigHandler.mobList) {
            ResourceLocation id = new ResourceLocation(value);

            if (EntityList.getEntityNameList().contains(id)) {
                mobs.add(id);
            } else {
                Champions.logger.log(Level.ERROR, "Invalid entity found in mob config! " + value);
            }
        }

        for (String value : ConfigHandler.championsList) {
            String[] args = value.split(";");
            ResourceLocation id = new ResourceLocation(args[0]);
            int minTier = args.length > 1 ? Integer.parseInt(args[1]) : 0;
            int maxTier = args.length > 2 ? Integer.parseInt(args[2]) : 0;

            if (EntityList.getEntityNameList().contains(id)) {
                champions.put(id, new Tuple<>(minTier, maxTier));
            } else {
                Champions.logger.log(Level.ERROR, "Invalid entity found in champions list config! " + value);
            }
        }
    }
}
