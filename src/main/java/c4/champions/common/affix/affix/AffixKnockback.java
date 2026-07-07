package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class AffixKnockback extends Affix {

    public AffixKnockback() {
        super("knockback", AffixCategory.CC);
    }

    @Override
    public void onKnockback(EntityLiving entity, Champion cap, LivingKnockBackEvent evt) {
        evt.setStrength(evt.getStrength() * (float)ConfigHandler.affix.knockback.multiplier);
        evt.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
    }
}
