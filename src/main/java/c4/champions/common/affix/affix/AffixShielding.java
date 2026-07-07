package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.affix.AffixState;
import c4.champions.common.champion.Champion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixShielding extends Affix {

    public AffixShielding() {
        super("shielding", AffixCategory.DEFENSE);
    }

    @Override
    public AffixState createState() {
        return new ShieldingState();
    }

    @Override
    public void onUpdate(EntityLiving entity, Champion cap) {
        ShieldingState shielding = cap.getState(this);

        if (!entity.world.isRemote) {

            if (entity.ticksExisted % 40 == 0 && entity.getRNG().nextFloat() < 0.5f) {
                shielding.mode = !shielding.mode;
                cap.markDirty(this);
            }
        } else if (shielding.mode) {
            entity.world.spawnParticle(EnumParticleTypes.SPELL_MOB, entity.posX + (entity.getRNG().nextDouble() - 0.5D)
                            * (double) entity.width, entity.posY + entity.getRNG().nextDouble() * (double) entity.height,
                    entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * (double) entity.width, 1, 1, 1);
        }
    }

    @Override
    public void onAttacked(EntityLiving entity, Champion cap, DamageSource source, float amount, LivingAttackEvent
                           evt) {
        ShieldingState shielding = cap.getState(this);

        if (shielding.mode) {
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents
                    .ENTITY_PLAYER_ATTACK_NODAMAGE, entity.getSoundCategory(), 1.0F, 1.0F);
            evt.setCanceled(true);
        }
    }

    public static class ShieldingState implements AffixState {

        boolean mode;

        @Override
        public void read(NBTTagCompound tag) {
            mode = tag.getBoolean("mode");
        }

        @Override
        public void write(NBTTagCompound tag) {
            tag.setBoolean("mode", mode);
        }
    }
}
