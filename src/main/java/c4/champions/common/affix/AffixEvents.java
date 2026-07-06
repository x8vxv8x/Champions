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

import c4.champions.Champions;
import c4.champions.common.champion.ChampionSync;
import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.Rank;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixEvents {

    @SubscribeEvent
    public void onLivingJoinWorld(EntityJoinWorldEvent evt) {
        Champion chp = ChampionCapability.get(evt.getEntity());

        if (chp != null) {
            chp.onSpawn((EntityLiving)evt.getEntity());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            EntityLiving living = (EntityLiving)evt.getEntityLiving();
            chp.onUpdate(living);
            ChampionSync.dirty(living);
            Rank rank = chp.getRank();

            if (chp.isElite()) {

                if (living.world.isRemote && !ConfigHandler.hideParticles) {
                    Champions.proxy.generateRankParticle(living, rank.getColor());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttacked(LivingAttackEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            chp.onAttacked((EntityLiving)evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent evt) {

        if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)evt.getSource().getTrueSource();
            Champion chp = ChampionCapability.get(entityLivingBase);

            if (chp != null) {
                EntityLiving living = (EntityLiving)entityLivingBase;
                chp.onAttack(living, evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt);
            }
        }
    }

    @SubscribeEvent
    public void onLivingWasHurt(LivingHurtEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            evt.setAmount(chp.onHurt((EntityLiving)evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt));
        }
    }

    @SubscribeEvent
    public void onLivingDamaged(LivingDamageEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            evt.setAmount(chp.onDamaged((EntityLiving)evt.getEntityLiving(), evt.getSource(), evt.getAmount(), evt));
        }
    }

    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent evt) {

        Champion chp = ChampionCapability.get(evt.getOriginalAttacker());

        if (chp != null) {
            chp.onKnockback((EntityLiving)evt.getOriginalAttacker(), evt);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            chp.onDeath((EntityLiving)evt.getEntityLiving(), evt.getSource(), evt);
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent evt) {

        Champion chp = ChampionCapability.get(evt.getEntityLiving());

        if (chp != null) {
            evt.setAmount(chp.onHealed((EntityLiving)evt.getEntityLiving(), evt.getAmount()));
        }
    }

}
