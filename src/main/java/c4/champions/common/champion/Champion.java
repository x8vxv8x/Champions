/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.common.champion;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixInstance;
import c4.champions.common.affix.AffixState;
import c4.champions.common.affix.filter.AffixFilter;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class Champion {

    private final Map<String, AffixInstance> affixes = Maps.newLinkedHashMap();
    private final Collection<AffixInstance> affixView = Collections.unmodifiableCollection(affixes.values());
    private final Set<String> dirtyAffixes = Sets.newHashSet();
    private Rank rank = null;
    private String name;

    private static final String AFFIX_TAG = "affixes";
    private static final String TIER_TAG = "tier";
    private static final String DATA_TAG = "data";
    private static final String NAME_TAG = "name";
    private static final String IDENTIFIER_TAG = "identifier";

    public Champion() {}

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public boolean isElite() {
        return rank != null && rank.getTier() > 0;
    }

    public Collection<AffixInstance> getAffixes() {
        return affixView;
    }

    public Set<String> getAffixIds() {
        return Sets.newLinkedHashSet(affixes.keySet());
    }

    public boolean hasAffix(String identifier) {
        return affixes.containsKey(identifier);
    }

    public void setAffixes(Collection<String> identifiers) {
        Map<String, AffixInstance> oldAffixes = Maps.newHashMap(affixes);
        affixes.clear();

        for (String identifier : identifiers) {
            Affix affix = AffixRegistry.getAffix(identifier);

            if (affix != null) {
                AffixInstance instance = oldAffixes.get(identifier);
                affixes.put(identifier, instance == null ? new AffixInstance(affix) : instance);
            }
        }
        dirtyAffixes.clear();
    }

    public void addAffix(String identifier) {
        if (!affixes.containsKey(identifier)) {
            Affix affix = AffixRegistry.getAffix(identifier);

            if (affix != null) {
                affixes.put(identifier, new AffixInstance(affix));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AffixState> T getState(Affix affix) {
        AffixInstance instance = getAffix(affix.getIdentifier());
        return instance == null ? null : (T)instance.getState();
    }

    @Nullable
    public AffixInstance getAffix(String identifier) {
        return affixes.get(identifier);
    }

    public void markDirty(Affix affix) {
        if (affix != null && affixes.containsKey(affix.getIdentifier())) {
            dirtyAffixes.add(affix.getIdentifier());
        }
    }

    public List<AffixInstance> drainDirtyAffixes() {
        List<AffixInstance> dirty = Lists.newArrayList();

        for (String identifier : dirtyAffixes) {
            AffixInstance instance = affixes.get(identifier);

            if (instance != null) {
                dirty.add(instance);
            }
        }
        dirtyAffixes.clear();
        return dirty;
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();

        if (rank == null) {
            return tag;
        }
        tag.setInteger(TIER_TAG, rank.getTier());

        if (isElite()) {
            tag.setTag(AFFIX_TAG, writeAffixes());
            tag.setString(NAME_TAG, name == null ? "" : name);
        }
        return tag;
    }

    public void read(NBTTagCompound tag) {
        read(tag, true);
    }

    public void read(NBTTagCompound tag, boolean requireEnabledFilter) {
        if (!tag.hasKey(TIER_TAG)) {
            return;
        }
        rank = RankManager.getRankForTier(tag.getInteger(TIER_TAG));
        name = tag.getString(NAME_TAG);
        readAffixes(tag.getTagList(AFFIX_TAG, Constants.NBT.TAG_COMPOUND), requireEnabledFilter);
    }

    public NBTTagList writeAffixes() {
        NBTTagList list = new NBTTagList();

        for (AffixInstance affix : affixes.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(IDENTIFIER_TAG, affix.getIdentifier());
            tag.setTag(DATA_TAG, affix.serializeState());
            list.appendTag(tag);
        }
        return list;
    }

    public void readAffixes(NBTTagList list) {
        readAffixes(list, false);
    }

    private void readAffixes(NBTTagList list, boolean requireEnabledFilter) {
        affixes.clear();
        dirtyAffixes.clear();

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            String id = tag.getString(IDENTIFIER_TAG);
            Affix affix = AffixRegistry.getAffix(id);

            if (affix != null && (!requireEnabledFilter || isFilterEnabled(id))) {
                AffixInstance instance = new AffixInstance(affix);
                instance.deserializeState(tag.getCompoundTag(DATA_TAG));
                affixes.put(id, instance);
            }
        }
    }

    private static boolean isFilterEnabled(String id) {
        AffixFilter filter = AffixFilterManager.getAffixFilter(id);
        return filter != null && filter.isEnabled();
    }

    public void updateAffixState(String identifier, NBTTagCompound tag) {
        AffixInstance instance = affixes.get(identifier);

        if (instance != null) {
            instance.deserializeState(tag);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void onSpawn(EntityLiving entity) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onSpawn(entity, this);
        }
        applyRankEffects(entity);
    }

    public void onUpdate(EntityLiving entity) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onUpdate(entity, this);
        }

        if (isElite()) {
            applyRankEffects(entity);
        }
    }

    public void onAttacked(EntityLiving entity, DamageSource source, float amount, LivingAttackEvent evt) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onAttacked(entity, this, source, amount, evt);
        }
    }

    public void onAttack(EntityLiving entity, EntityLivingBase target, DamageSource source, float amount,
                         LivingAttackEvent evt) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onAttack(entity, this, target, source, amount, evt);
        }
    }

    public float onHurt(EntityLiving entity, DamageSource source, float amount, LivingHurtEvent evt) {
        float newAmount = amount;

        for (AffixInstance affix : getAffixes()) {
            newAmount = affix.getAffix().onHurt(entity, this, source, amount, newAmount, evt);
        }
        return newAmount;
    }

    public float onDamaged(EntityLiving entity, DamageSource source, float amount, LivingDamageEvent evt) {
        float newAmount = amount;

        for (AffixInstance affix : getAffixes()) {
            newAmount = affix.getAffix().onDamaged(entity, this, source, amount, newAmount, evt);
        }
        return newAmount;
    }

    public float onHealed(EntityLiving entity, float amount) {
        float newAmount = amount;

        for (AffixInstance affix : getAffixes()) {
            newAmount = affix.getAffix().onHealed(entity, this, amount, newAmount);
        }
        return newAmount;
    }

    public void onKnockback(EntityLiving entity, LivingKnockBackEvent evt) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onKnockback(entity, this, evt);
        }
    }

    public void onDeath(EntityLiving entity, DamageSource source, LivingDeathEvent evt) {
        for (AffixInstance affix : getAffixes()) {
            affix.getAffix().onDeath(entity, this, source, evt);
        }
    }

    public void applyRankEffects(EntityLiving entity) {
        if (entity.world.isRemote || !isElite() ||
                (entity.ticksExisted > 0 && entity.ticksExisted % 100 != 0)) {
            return;
        }
        List<Tuple<Potion, Integer>> potions = RankManager.getPotionsForTier(rank.getTier());

        if (potions != null) {
            for (Tuple<Potion, Integer> potion : potions) {
                entity.addPotionEffect(new PotionEffect(potion.getFirst(), 200, potion.getSecond()));
            }
        }
    }
}
