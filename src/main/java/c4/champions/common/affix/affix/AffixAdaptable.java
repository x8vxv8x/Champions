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
import c4.champions.common.affix.AffixState;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class AffixAdaptable extends Affix {

    public AffixAdaptable() {
        super("adaptable", AffixCategory.DEFENSE);
    }

    @Override
    public AffixState createState() {
        return new DamageType();
    }

    @Override
    public float onHurt(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                        LivingHurtEvent evt) {
        String type = source.getDamageType();
        DamageType damageType = cap.getState(this);

        if (damageType.name.equalsIgnoreCase(type)) {
            newAmount -= (float) (amount * ConfigHandler.affix.adaptable.damageReductionIncrement * damageType.count);
            damageType.count++;
        } else {
            damageType.name = type;
            damageType.count = 0;
        }
        cap.markDirty(this);
        return Math.max(amount * (float)(1.0f - ConfigHandler.affix.adaptable.maxDamageReduction), newAmount);
    }

    public static class DamageType implements AffixState {

        String name = "";
        int count;

        @Override
        public void read(NBTTagCompound tag) {
            name = tag.getString("name");
            count = tag.getInteger("count");
        }

        @Override
        public void write(NBTTagCompound tag) {
            tag.setString("name", name);
            tag.setInteger("count", count);
        }
    }
}
