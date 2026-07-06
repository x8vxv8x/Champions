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

package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AffixHasty extends Affix {

    public AffixHasty() {
        super("hasty", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, Champion cap) {
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        speed.setBaseValue(speed.getBaseValue() + ConfigHandler.affix.hasty.movementBonus);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null;
    }
}
