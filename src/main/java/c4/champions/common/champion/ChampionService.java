package c4.champions.common.champion;

import c4.champions.Champions;
import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.affix.AffixInstance;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.potion.PotionPlague;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.integrations.gamestages.ChampionStages;
import c4.champions.integrations.scalinghealth.ChampionDifficulty;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.smd.scalinghealth.api.ScalingHealthAPI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Level;

public final class ChampionService {

    private static final Random rand = new Random();
    private static final Map<Integer, List<LootData>> drops = Maps.newHashMap();

    private ChampionService() {
    }

    public static void parseConfigs() {
        Potion potion = Potion.getPotionFromResourceLocation(ConfigHandler.affix.plagued.infectPotion);

        if (potion != null) {
            PotionPlague.setInfectionPotion(potion);
        }
        ChampionRules.parseConfig();
        parseLootConfig();
    }

    public static Rank generateRank(EntityLiving entity) {
        ImmutableSortedMap<Integer, Rank> ranks = RankManager.getRanks();
        int finalTier = 0;
        int firstTier = ranks.firstKey();
        float chance = chance(entity, firstTier);
        Tuple<Integer, Integer> curated = ChampionRules.curatedTier(entity);

        if (curated != null) {
            if (curated.getFirst() > 0) {
                finalTier = curated.getFirst();

                if (curated.getSecond() == 0) {
                    return ranks.get(finalTier);
                }
            } else {
                finalTier = firstTier;
            }
        } else if (rand.nextFloat() < chance) {
            if ((Champions.isGameStagesLoaded && !ChampionStages.isValidTier(firstTier, entity)) ||
                    ChampionRules.nearActiveBeacon(entity)) {
                return RankManager.getEmptyRank();
            }
            finalTier = firstTier;
        }

        if (finalTier > 0) {
            for (Integer tier : ranks.keySet().tailSet(finalTier, false)) {
                if (rand.nextFloat() >= chance(entity, tier)) {
                    break;
                }
                if (Champions.isGameStagesLoaded && !ChampionStages.isValidTier(tier, entity)) {
                    break;
                }
                finalTier = tier;
            }
        }

        if (finalTier == 0) {
            return RankManager.getEmptyRank();
        }
        if (curated != null && curated.getSecond() > 0) {
            finalTier = Math.min(finalTier, curated.getSecond());
        }
        return ranks.get(finalTier);
    }

    public static void apply(EntityLiving living, Rank rank) {
        apply(living, rank, null);
    }

    public static void apply(EntityLiving living, Rank rank, @Nullable Set<String> affixes) {
        Champion champion = ChampionCapability.get(living);

        if (champion == null || rank == null) {
            return;
        }
        champion.setRank(rank);

        if (rank.getTier() <= 0) {
            return;
        }
        champion.setAffixes(affixes == null || affixes.isEmpty() ? generateAffixes(rank, living) : Sets.newHashSet(affixes));
        champion.setName(randomName());
        rank.applyGrowth(living);

        for (AffixInstance affix : champion.getAffixes()) {
            affix.getAffix().onInitialSpawn(living, champion);
        }
    }

    public static boolean setTier(EntityLiving living, int tier) {
        Champion champion = ChampionCapability.get(living);
        Rank rank = rank(tier);

        if (champion == null || rank == null) {
            return false;
        }
        boolean wasChampion = champion.isElite();
        champion.setRank(rank);

        if (rank.getTier() <= 0) {
            champion.setAffixes(Sets.newHashSet());
            champion.setName("");
        } else if (champion.getName() == null || champion.getName().isEmpty()) {
            champion.setName(randomName());
        }

        if (!wasChampion && rank.getTier() > 0) {
            rank.applyGrowth(living);
        }
        ChampionSync.full(living);
        return true;
    }

    public static boolean setName(EntityLiving living, String name) {
        Champion champion = ChampionCapability.get(living);

        if (champion == null) {
            return false;
        }
        champion.setName(name);
        ChampionSync.full(living);
        return true;
    }

    public static boolean addAffix(EntityLiving living, String identifier) {
        Champion champion = ChampionCapability.get(living);
        Affix affix = AffixRegistry.getAffix(identifier);

        if (champion == null || affix == null || !champion.isElite()) {
            return false;
        }
        if (!champion.hasAffix(identifier)) {
            champion.addAffix(identifier);
            affix.onInitialSpawn(living, champion);
            ChampionSync.full(living);
        }
        return true;
    }

    public static boolean setAffixes(EntityLiving living, Collection<String> identifiers) {
        Champion champion = ChampionCapability.get(living);
        Set<String> affixes = validAffixes(identifiers);

        if (champion == null || affixes == null || !champion.isElite()) {
            return false;
        }
        setAffixes(living, champion, affixes);
        ChampionSync.full(living);
        return true;
    }

    public static boolean set(EntityLiving living, int tier, Collection<String> identifiers, @Nullable String name,
                              boolean randomName) {
        Champion champion = ChampionCapability.get(living);
        Rank rank = rank(tier);
        Set<String> affixes = validAffixes(identifiers);

        if (champion == null || rank == null || rank.getTier() <= 0 || affixes == null) {
            return false;
        }
        boolean wasChampion = champion.isElite();
        champion.setRank(rank);
        setAffixes(living, champion, affixes);
        champion.setName(randomName || name == null || name.isEmpty() ? randomName() : name);

        if (!wasChampion) {
            rank.applyGrowth(living);
        }
        ChampionSync.full(living);
        return true;
    }

    public static String randomName() {
        int langSize = 24;
        int randomPrefix = rand.nextInt(langSize + ConfigHandler.championNames.length);
        int randomSuffix = rand.nextInt(langSize + ConfigHandler.championNameSuffixes.length);
        String header = Champions.MODID + ".%s.%d";
        String prefix = randomPrefix < langSize
                ? new TextComponentTranslation(String.format(header, "prefix", randomPrefix)).getFormattedText()
                : ConfigHandler.championNames[randomPrefix - langSize];

        if (randomSuffix < langSize) {
            return prefix + new TextComponentTranslation(String.format(header, "suffix", randomSuffix)).getFormattedText();
        }
        String suffix = ConfigHandler.championNameSuffixes[randomSuffix - langSize];
        return prefix + (suffix.isEmpty() || suffix.charAt(0) == ',' ? suffix : " " + suffix);
    }

    public static List<ItemStack> getLootDrops(int tier) {
        List<LootData> data = Lists.newArrayList(drops.getOrDefault(tier, Lists.newArrayList()));
        List<ItemStack> result = new ArrayList<>();

        for (int i = 0, amount = ConfigHandler.lootScaling ? tier : 1; i < amount && !data.isEmpty(); i++) {
            double totalWeight = 0;

            for (LootData loot : data) {
                totalWeight += loot.weight;
            }
            double random = rand.nextDouble() * totalWeight;
            double countWeight = 0;

            for (LootData loot : data) {
                countWeight += loot.weight;

                if (countWeight >= random) {
                    result.add(loot.getLootStack());
                    break;
                }
            }
        }
        return result;
    }

    private static float chance(EntityLiving entity, int tier) {
        float chance = RankManager.getRanks().get(tier).getChance();

        if (Champions.isScalingHealthLoaded) {
            chance += (float)(ChampionDifficulty.getSpawnModifier(tier) *
                    ScalingHealthAPI.getAreaDifficulty(entity.world, entity.getPosition()));
        }
        return chance;
    }

    @Nullable
    private static Rank rank(int tier) {
        Rank rank = RankManager.getRankForTier(tier);
        return tier <= 0 || rank.getTier() > 0 ? rank : null;
    }

    @Nullable
    private static Set<String> validAffixes(@Nullable Collection<String> identifiers) {
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

    private static void setAffixes(EntityLiving living, Champion champion, Set<String> identifiers) {
        Set<String> oldAffixes = champion.getAffixIds();
        champion.setAffixes(identifiers);

        for (String identifier : identifiers) {
            if (!oldAffixes.contains(identifier)) {
                Affix affix = AffixRegistry.getAffix(identifier);

                if (affix != null) {
                    affix.onInitialSpawn(living, champion);
                }
            }
        }
    }

    private static Set<String> generateAffixes(Rank rank, EntityLiving entity, String... presets) {
        int tier = rank.getTier();
        Set<String> affixList = Sets.newHashSet();
        Map<AffixCategory, Set<String>> categoryMap = AffixRegistry.getCategoryMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Sets.newHashSet(e.getValue())));
        Set<String> curatedPresets = Sets.newHashSet(presets);
        curatedPresets.addAll(AffixFilterManager.getPresetAffixesForEntity(entity));

        for (String id : curatedPresets) {
            Affix affix = AffixRegistry.getAffix(id);

            if (affix != null && addAffixIfValid(affixList, categoryMap, affix, entity, tier, true)) {
                Set<String> available = categoryMap.get(affix.getCategory());

                if (available == null || available.isEmpty() || affix.getCategory() != AffixCategory.OFFENSE) {
                    categoryMap.remove(affix.getCategory());
                }
            }
        }

        while (!categoryMap.isEmpty() && affixList.size() < rank.getAffixes()) {
            AffixCategory category = categoryMap.keySet().toArray(new AffixCategory[0])[rand.nextInt(categoryMap.size())];
            Set<String> available = categoryMap.get(category);
            String id = randomElement(available);
            Affix affix = AffixRegistry.getAffix(id);
            boolean added = affix != null && addAffixIfValid(affixList, categoryMap, affix, entity, tier, false);

            if (added && category != AffixCategory.OFFENSE) {
                categoryMap.remove(category);
            } else {
                available.remove(id);

                if (available.isEmpty()) {
                    categoryMap.remove(category);
                }
            }
        }
        return affixList;
    }

    private static boolean addAffixIfValid(Set<String> selected, Map<AffixCategory, Set<String>> availableByCategory,
                                           Affix affix, EntityLiving entity, int tier, boolean preset) {
        Set<String> available = availableByCategory.get(affix.getCategory());

        if (available == null || !available.remove(affix.getIdentifier())) {
            return false;
        }
        if (!preset && (!affix.canApply(entity) || !AffixFilterManager.isValidAffix(affix, entity, tier))) {
            return false;
        }

        for (String id : selected) {
            if (!affix.isCompatibleWith(AffixRegistry.getAffix(id))) {
                return false;
            }
        }
        selected.add(affix.getIdentifier());
        return true;
    }

    private static String randomElement(Set<String> values) {
        int index = rand.nextInt(values.size());
        Iterator<String> iter = values.iterator();

        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }

    private static void parseLootConfig() {
        drops.clear();

        for (String value : ConfigHandler.lootDrops) {
            String[] parsed = value.split(";");

            if (parsed.length < 2) {
                Champions.logger.log(Level.ERROR, value + " needs at least a tier and an item name");
                continue;
            }

            try {
                int tier = Integer.parseInt(parsed[0]);
                Item item = Item.getByNameOrId(parsed[1]);

                if (item == null) {
                    Champions.logger.log(Level.ERROR, "Item not found!" + parsed[1]);
                    continue;
                }
                int metadata = parsed.length > 2 ? Integer.parseInt(parsed[2]) : 0;
                int stackSize = parsed.length > 3 ? Integer.parseInt(parsed[3]) : 1;
                boolean enchant = parsed.length > 4 && parsed[4].equalsIgnoreCase("true");
                int weight = parsed.length > 5 ? Integer.parseInt(parsed[5]) : 1;
                drops.computeIfAbsent(tier, key -> Lists.newArrayList())
                        .add(new LootData(new ItemStack(item, stackSize, metadata), enchant, weight));
            } catch (NumberFormatException e) {
                Champions.logger.log(Level.ERROR, "Invalid loot drop config: " + value);
            }
        }
    }

    private static class LootData {

        private final ItemStack stack;
        private final boolean enchant;
        private final int weight;

        LootData(ItemStack stack, boolean enchant, int weight) {
            this.stack = stack;
            this.enchant = enchant;
            this.weight = weight;
        }

        ItemStack getLootStack() {
            ItemStack loot = stack.copy();

            if (enchant) {
                EnchantmentHelper.addRandomEnchantment(rand, loot, 30, true);
            }
            return loot;
        }
    }
}
