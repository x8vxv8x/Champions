package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.affix.AffixState;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class AffixLively extends Affix {

    private static final String KEY_LAST_ATTACK = "lastAttackTime";

    private final int cooldownTicks = ConfigHandler.affix.lively.cooldown * 20;
    private final double healAmount = ConfigHandler.affix.lively.healAmount;
    private final double passiveMultiplier = ConfigHandler.affix.lively.passiveMultiplier;

    public AffixLively() {
        super("lively", AffixCategory.DEFENSE);
    }

    @Override
    public AffixState createState() {
        return new LivelyState();
    }

    @Override
    public void onSpawn(EntityLiving entity, Champion cap) {
        super.onSpawn(entity, cap);
    }

    @Override
    public void onUpdate(EntityLiving entity, Champion cap) {

        if (entity.world.isRemote || !entity.isEntityAlive()) {
            return;
        }
        if (entity.ticksExisted % 20 != 0) {
            return;
        }

        if (entity.getHealth() >= entity.getMaxHealth()) {
            return;
        }

        int lastAttack = cap.<LivelyState>getState(this).lastAttack;
        if ((lastAttack + cooldownTicks) < entity.world.getTotalWorldTime()) {
            double amount = healAmount;
            if (entity.getAttackTarget() == null) {
                amount *= passiveMultiplier;
            }
            entity.heal((float) amount);
        }
    }

    @Override
    public float onDamaged(EntityLiving entity, Champion cap, DamageSource source,
                           float amount, float newAmount, LivingDamageEvent evt) {
        cap.<LivelyState>getState(this).lastAttack = (int) entity.world.getTotalWorldTime();
        cap.markDirty(this);
        return super.onDamaged(entity, cap, source, amount, newAmount, evt);
    }

    public static class LivelyState implements AffixState {

        int lastAttack;

        @Override
        public void read(NBTTagCompound tag) {
            lastAttack = tag.getInteger(KEY_LAST_ATTACK);
        }

        @Override
        public void write(NBTTagCompound tag) {
            tag.setInteger(KEY_LAST_ATTACK, lastAttack);
        }
    }
}
