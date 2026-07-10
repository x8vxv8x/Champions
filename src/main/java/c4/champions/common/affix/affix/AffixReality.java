package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class AffixReality extends Affix {

    public AffixReality() {
        super("reality", AffixCategory.DEFENSE);
    }

    @Override
    public float onHurt(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                        LivingHurtEvent evt) {
        if (source.isMagicDamage()) {
            return newAmount * 0.05F;
        }
        return newAmount * 2.0F;
    }
}
