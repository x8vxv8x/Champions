package c4.champions.integrations.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.event.EntityLivingAttackedEvent;
import crafttweaker.api.event.EntityLivingDamageEvent;
import crafttweaker.api.event.EntityLivingDeathEvent;
import crafttweaker.api.event.EntityLivingHurtEvent;
import crafttweaker.api.event.LivingKnockBackEvent;
import stanhebben.zenscript.annotations.ZenClass;

public final class ScriptedAffixFunctions {

    private ScriptedAffixFunctions() {}

    @ZenClass("mods.champions.affix.OnInitialSpawn")
    @ZenRegister
    public interface OnInitialSpawn {
        void handle(IEntityLiving living);
    }

    @ZenClass("mods.champions.affix.OnSpawn")
    @ZenRegister
    public interface OnSpawn {
        void handle(IEntityLiving living);
    }

    @ZenClass("mods.champions.affix.OnUpdate")
    @ZenRegister
    public interface OnUpdate {
        void handle(IEntityLiving living);
    }

    @ZenClass("mods.champions.affix.OnAttack")
    @ZenRegister
    public interface OnAttack {
        void handle(IEntityLiving living, IEntityLivingBase target, IDamageSource source, float amount,
                    EntityLivingAttackedEvent event);
    }

    @ZenClass("mods.champions.affix.OnAttacked")
    @ZenRegister
    public interface OnAttacked {
        void handle(IEntityLiving living, IDamageSource source, float amount, EntityLivingAttackedEvent event);
    }

    @ZenClass("mods.champions.affix.OnHurt")
    @ZenRegister
    public interface OnHurt {
        float handle(IEntityLiving living, IDamageSource source, float amount, float newAmount,
                     EntityLivingHurtEvent event);
    }

    @ZenClass("mods.champions.affix.OnHealed")
    @ZenRegister
    public interface OnHealed {
        float handle(IEntityLiving living, float amount, float newAmount);
    }

    @ZenClass("mods.champions.affix.OnDamaged")
    @ZenRegister
    public interface OnDamaged {
        float handle(IEntityLiving living, IDamageSource source, float amount, float newAmount,
                     EntityLivingDamageEvent event);
    }

    @ZenClass("mods.champions.affix.OnDeath")
    @ZenRegister
    public interface OnDeath {
        void handle(IEntityLiving living, IDamageSource source, EntityLivingDeathEvent event);
    }

    @ZenClass("mods.champions.affix.OnKnockback")
    @ZenRegister
    public interface OnKnockback {
        void handle(IEntityLiving living, LivingKnockBackEvent event);
    }

    @ZenClass("mods.champions.affix.CanApply")
    @ZenRegister
    public interface CanApply {
        boolean handle(IEntityLiving living);
    }
}
