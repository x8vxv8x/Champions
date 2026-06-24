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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

package c4.champions.integrations.crafttweaker;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixCategory;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import java.util.Locale;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.champions.AffixBuilder")
public class ScriptedAffixBuilder {

    private final String id;
    private final AffixCategory category;

    @ZenProperty
    public int tier = 1;

    @ZenProperty
    public ScriptedAffixFunctions.CanApply canApply;

    @ZenProperty
    public ScriptedAffixFunctions.OnAttacked onAttacked;

    @ZenProperty
    public ScriptedAffixFunctions.OnHurt onHurt;

    @ZenProperty
    public ScriptedAffixFunctions.OnInitialSpawn onInitialSpawn;

    @ZenProperty
    public ScriptedAffixFunctions.OnSpawn onSpawn;

    @ZenProperty
    public ScriptedAffixFunctions.OnAttack onAttack;

    @ZenProperty
    public ScriptedAffixFunctions.OnDamaged onDamaged;

    @ZenProperty
    public ScriptedAffixFunctions.OnDeath onDeath;

    @ZenProperty
    public ScriptedAffixFunctions.OnHealed onHealed;

    @ZenProperty
    public ScriptedAffixFunctions.OnKnockback onKnockback;

    @ZenProperty
    public ScriptedAffixFunctions.OnUpdate onUpdate;

    private ScriptedAffixBuilder(String id, String category, int tier) {
        this.id = id;
        this.category = parseCategory(category);
        this.tier = tier;
    }

    @ZenMethod
    public static ScriptedAffixBuilder create(String id, String category) {
        return new ScriptedAffixBuilder(id, category, 1);
    }

    @ZenMethod
    public static ScriptedAffixBuilder create(String id, String category, int tier) {
        return new ScriptedAffixBuilder(id, category, tier);
    }

    @ZenMethod
    public boolean register() {
        if (id == null || id.isEmpty()) {
            CraftTweakerAPI.logError("Cannot register Champions affix with an empty identifier");
            return false;
        }

        if (AffixRegistry.getAffix(id) != null) {
            CraftTweakerAPI.logError("Cannot register duplicate Champions affix: " + id);
            return false;
        }
        AffixRegistry.registerAffix(new ScriptedAffix(this));
        CraftTweakerAPI.logInfo("Registered Champions affix: " + id);
        return true;
    }

    String getId() {
        return id;
    }

    AffixCategory getCategory() {
        return category;
    }

    private static AffixCategory parseCategory(String category) {
        try {
            return AffixCategory.valueOf(category.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown Champions affix category: " + category +
                    ". Expected one of: CC, OFFENSE, DEFENSE", e);
        }
    }
}
