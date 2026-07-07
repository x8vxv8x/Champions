package c4.champions.integrations.crafttweaker;

import c4.champions.common.affix.Affix;
import c4.champions.common.champion.Champion;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.events.handling.MCEntityLivingAttackedEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDamageEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDeathEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingHurtEvent;
import crafttweaker.mc1120.events.handling.MCLivingKnockBackEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

class ScriptedAffix extends Affix {

    private final ScriptedAffixFunctions.CanApply canApply;
    private final ScriptedAffixFunctions.OnAttacked onAttacked;
    private final ScriptedAffixFunctions.OnHurt onHurt;
    private final ScriptedAffixFunctions.OnInitialSpawn onInitialSpawn;
    private final ScriptedAffixFunctions.OnSpawn onSpawn;
    private final ScriptedAffixFunctions.OnAttack onAttack;
    private final ScriptedAffixFunctions.OnDamaged onDamaged;
    private final ScriptedAffixFunctions.OnDeath onDeath;
    private final ScriptedAffixFunctions.OnHealed onHealed;
    private final ScriptedAffixFunctions.OnKnockback onKnockback;
    private final ScriptedAffixFunctions.OnUpdate onUpdate;

    ScriptedAffix(ScriptedAffixBuilder builder) {
        super(builder.getId(), builder.getCategory(), builder.tier);
        this.canApply = builder.canApply;
        this.onAttacked = builder.onAttacked;
        this.onHurt = builder.onHurt;
        this.onInitialSpawn = builder.onInitialSpawn;
        this.onSpawn = builder.onSpawn;
        this.onAttack = builder.onAttack;
        this.onDamaged = builder.onDamaged;
        this.onDeath = builder.onDeath;
        this.onHealed = builder.onHealed;
        this.onKnockback = builder.onKnockback;
        this.onUpdate = builder.onUpdate;
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, Champion cap) {
        if (onInitialSpawn != null) {
            onInitialSpawn.handle(CraftTweakerMC.getIEntityLiving(entity));
        }
    }

    @Override
    public void onSpawn(EntityLiving entity, Champion cap) {
        if (onSpawn != null) {
            onSpawn.handle(CraftTweakerMC.getIEntityLiving(entity));
        }
    }

    @Override
    public void onUpdate(EntityLiving entity, Champion cap) {
        if (onUpdate != null) {
            onUpdate.handle(CraftTweakerMC.getIEntityLiving(entity));
        }
    }

    @Override
    public void onAttack(EntityLiving entity, Champion cap, EntityLivingBase target, DamageSource source,
                         float amount, LivingAttackEvent evt) {
        if (onAttack != null) {
            onAttack.handle(CraftTweakerMC.getIEntityLiving(entity), CraftTweakerMC.getIEntityLivingBase(target),
                    CraftTweakerMC.getIDamageSource(source), amount, new MCEntityLivingAttackedEvent(evt));
        }
    }

    @Override
    public void onAttacked(EntityLiving entity, Champion cap, DamageSource source, float amount,
                           LivingAttackEvent evt) {
        if (onAttacked != null) {
            onAttacked.handle(CraftTweakerMC.getIEntityLiving(entity), CraftTweakerMC.getIDamageSource(source), amount,
                    new MCEntityLivingAttackedEvent(evt));
        }
    }

    @Override
    public float onHurt(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                        LivingHurtEvent evt) {
        return onHurt == null ? newAmount : onHurt.handle(CraftTweakerMC.getIEntityLiving(entity),
                CraftTweakerMC.getIDamageSource(source), amount, newAmount, new MCEntityLivingHurtEvent(evt));
    }

    @Override
    public float onDamaged(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                           LivingDamageEvent evt) {
        return onDamaged == null ? newAmount : onDamaged.handle(CraftTweakerMC.getIEntityLiving(entity),
                CraftTweakerMC.getIDamageSource(source), amount, newAmount, new MCEntityLivingDamageEvent(evt));
    }

    @Override
    public float onHealed(EntityLiving entity, Champion cap, float amount, float newAmount) {
        return onHealed == null ? newAmount : onHealed.handle(CraftTweakerMC.getIEntityLiving(entity), amount,
                newAmount);
    }

    @Override
    public void onDeath(EntityLiving entity, Champion cap, DamageSource source, LivingDeathEvent evt) {
        if (onDeath != null) {
            onDeath.handle(CraftTweakerMC.getIEntityLiving(entity), CraftTweakerMC.getIDamageSource(source),
                    new MCEntityLivingDeathEvent(evt));
        }
    }

    @Override
    public void onKnockback(EntityLiving entity, Champion cap, LivingKnockBackEvent evt) {
        if (onKnockback != null) {
            onKnockback.handle(CraftTweakerMC.getIEntityLiving(entity), new MCLivingKnockBackEvent(evt));
        }
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return canApply == null || canApply.handle(CraftTweakerMC.getIEntityLiving(entity));
    }
}
