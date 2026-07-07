package c4.champions.common.affix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class AffixRegistry {

    private static final Map<String, Affix> affixMap = Maps.newHashMap();
    private static final Map<AffixCategory, Set<String>> categoryMap = Maps.newEnumMap(AffixCategory.class);

    public static void registerAffix(Affix affix) {
        if (affix == null) {
            throw new IllegalArgumentException("Cannot register null affix");
        }
        registerAffix(affix.getIdentifier(), affix);
    }

    public static void registerAffix(String identifier, Affix affix) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Affix identifier cannot be null or empty");
        }
        if (affix == null) {
            throw new IllegalArgumentException("Cannot register null affix for identifier " + identifier);
        }
        if (affixMap.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate affix identifier: " + identifier);
        }
        affixMap.put(identifier, affix);
        categoryMap.computeIfAbsent(affix.getCategory(), k -> Sets.newHashSet()).add(identifier);
    }

    @Nullable
    public static Affix getAffix(String identifier) {
        return affixMap.get(identifier);
    }

    public static ImmutableList<Affix> getAllAffixes() {
        return ImmutableList.copyOf(affixMap.values());
    }

    public static ImmutableMap<AffixCategory, Set<String>> getCategoryMap() {
        return ImmutableMap.copyOf(categoryMap);
    }

}
