package c4.champions.integrations.crafttweaker;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.AffixCategory;
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
