package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class AffixDampening extends Affix {

    public AffixDampening() {
        super("dampening", AffixCategory.DEFENSE);
    }

    @Override
    public float onHurt(EntityLiving entity, Champion cap, DamageSource source, float amount, float newAmount,
                        LivingHurtEvent evt) {
        return source instanceof EntityDamageSourceIndirect ? newAmount * (float)(1.0f - ConfigHandler.affix.dampening
                .damageReduction) : newAmount;
    }
}
