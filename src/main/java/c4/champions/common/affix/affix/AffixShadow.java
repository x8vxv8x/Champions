package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class AffixShadow extends Affix {

    private static final int INTERVAL = 3 * 20;          //触发间隔 3 秒
    private static final int EFFECT_DURATION = 4 * 20;   //药水持续时间 4秒
    private static final int LIGHT_THRESHOLD = 6;        //触发最高亮度 6

    public AffixShadow() {
        super("shadow", AffixCategory.DEFENSE);
    }

    @Override
    public void onUpdate(EntityLiving entity, Champion cap) {

        if (entity.world.isRemote || !entity.isEntityAlive()) {
            return;
        }
        if (entity.ticksExisted % INTERVAL != 0) {
            return;
        }

        if (entity.getEntityWorld().getLight(entity.getPosition()) < LIGHT_THRESHOLD) {
            entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, EFFECT_DURATION));
        }
    }
}
