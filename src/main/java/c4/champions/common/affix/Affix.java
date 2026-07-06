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

package c4.champions.common.affix;

import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public abstract class Affix {

    protected static final Random rand = new Random();

    private final String identifier;
    private final AffixCategory category;
    private final int tier;

    public Affix(String identifier, AffixCategory category) {
        this(identifier, category, 1);
    }

    public Affix(String identifier, AffixCategory category, int tier) {
        this.identifier = identifier;
        this.category = category;
        this.tier = tier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public AffixCategory getCategory() {
        return category;
    }

    public AffixState createState() {
        return AffixState.EMPTY;
    }

    public void onInitialSpawn(EntityLiving entity, Champion cap) {

    }

    public void onSpawn(EntityLiving entity, Champion cap) {

    }

    public void onUpdate(EntityLiving entity, Champion cap) {

    }

    public void onAttack(EntityLiving entity, Champion cap, EntityLivingBase target, DamageSource source, float
            amount, LivingAttackEvent evt) {

    }

    public void onAttacked(EntityLiving entity, Champion cap, DamageSource source, float amount, LivingAttackEvent evt) {

    }

    public float onHurt(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                        LivingHurtEvent evt) {
        return newAmount;
    }

    public float onDamaged(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                           LivingDamageEvent evt) {
        return newAmount;
    }

    public float onHealed(EntityLiving entity, Champion cap, float amount, float newAmount) {
        return newAmount;
    }

    public void onKnockback(EntityLiving entity, Champion cap, LivingKnockBackEvent evt) {

    }

    public void onDeath(EntityLiving entity, Champion cap, DamageSource source, LivingDeathEvent evt) {

    }

    public boolean canApply(EntityLiving entity) {
        return true;
    }

    public boolean isCompatibleWith(Affix affix) {
        return affix != this;
    }

    public int getTier() {
        return tier;
    }

    public static boolean isValidAffixTarget(EntityLiving mob, EntityLivingBase target, boolean checkSight) {

        if (target == null || !target.isEntityAlive()) {
            return false;
        }

        if (checkSight && !mob.canEntityBeSeen(target)) {
            return false;
        }
        IAttributeInstance iattributeinstance = mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        double targetRange = iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        targetRange = ConfigHandler.affix.abilityRange == 0 ? targetRange : Math.min(targetRange, ConfigHandler.affix
                .abilityRange);
        return mob.getDistance(target) <= targetRange;
    }
}
