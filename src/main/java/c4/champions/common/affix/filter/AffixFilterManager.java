package c4.champions.common.affix.filter;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.Affix;
import c4.champions.common.util.JsonUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class AffixFilterManager {

    private static final Map<String, AffixFilter> FILTERS = Maps.newHashMap();
    private static final Map<String, Set<String>> ENTITY_AFFIX_MAP = Maps.newHashMap();

    @Nullable
    public static AffixFilter getAffixFilter(String identifier) {
        return FILTERS.get(identifier);
    }

    public static boolean isValidAffix(Affix affix, EntityLiving entityLiving, int tier) {
        AffixFilter filter = getAffixFilter(affix.getIdentifier());
        boolean hasTier = affix.getTier() <= tier;

        if (filter != null) {
            hasTier = filter.getTier() <= tier;
            return filter.isEnabled() && hasTier && !isEntityBlacklisted(filter, entityLiving);
        }
        return hasTier;
    }

    public static boolean isEntityBlacklisted(@Nonnull AffixFilter filter, Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);

        if (rl != null) {

            for (String key : filter.getEntityBlacklist()) {

                if (key.equals(rl.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    public static Set<String> getPresetAffixesForEntity(Entity entity) {
        ResourceLocation rl = EntityList.getKey(entity);
        return rl != null ? ENTITY_AFFIX_MAP.getOrDefault(rl.toString(), Sets.newHashSet()) : Sets.newHashSet();
    }

    public static void readAffixFiltersFromJson() {
        File file = new File(Loader.instance().getConfigDir(), Champions.MODID + "/affixes.json");
        TypeToken<AffixFilter[]> token = TypeToken.get(AffixFilter[].class);
        AffixFilter[] defaults = buildDefaultAffixFilters();
        AffixFilter[] filters = mergeMissingAffixFilters(JsonUtil.fromJson(token, file, defaults), defaults, file,
                token);

        for (AffixFilter filter : filters) {
            if (filter == null) {
                continue;
            }
            FILTERS.put(filter.getIdentifier(), filter);
            String[] alwaysOn = filter.getAlwaysOnEntity();

            for (String entityName : alwaysOn) {
                Set<String> affixes = ENTITY_AFFIX_MAP.getOrDefault(entityName, Sets.newHashSet());
                affixes.add(filter.getIdentifier());
                ENTITY_AFFIX_MAP.putIfAbsent(entityName, affixes);
            }
        }
    }

    private static AffixFilter[] mergeMissingAffixFilters(@Nullable AffixFilter[] filters, AffixFilter[] defaults,
                                                          File file, TypeToken<AffixFilter[]> token) {
        List<AffixFilter> merged = Lists.newArrayList();
        Set<String> existingIds = Sets.newHashSet();

        if (filters != null) {
            for (AffixFilter filter : filters) {
                if (filter != null) {
                    merged.add(filter);
                    existingIds.add(filter.getIdentifier());
                }
            }
        }
        boolean changed = false;

        for (AffixFilter filter : defaults) {
            if (!existingIds.contains(filter.getIdentifier())) {
                merged.add(filter);
                changed = true;
            }
        }

        AffixFilter[] result = merged.toArray(new AffixFilter[0]);

        if (changed) {
            JsonUtil.toJson(token, file, result);
        }
        return result;
    }

    private static AffixFilter[] buildDefaultAffixFilters() {
        ImmutableList<Affix> affixes = AffixRegistry.getAllAffixes();
        List<AffixFilter> filters = Lists.newArrayList();

        for (Affix aff : affixes) {
            filters.add(new AffixFilter(aff.getIdentifier(), true, new String[]{}, new String[]{}, aff.getTier()));
        }
        AffixFilter[] arr = new AffixFilter[filters.size()];
        return filters.toArray(arr);
    }
}
