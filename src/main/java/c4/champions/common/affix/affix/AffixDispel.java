package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.stream.Collectors;

public class AffixDispel extends Affix {

    public AffixDispel() {
        super("dispel", AffixCategory.OFFENSE);
    }

    @Override
    public void onAttack(EntityLiving entity, Champion cap, EntityLivingBase target, DamageSource source,
                         float amount, LivingAttackEvent evt) {
        if (entity.world.isRemote || !target.isEntityAlive()) {
            return;
        }

        List<PotionEffect> beneficial = target.getActivePotionEffects().stream()
                .filter(e -> !e.getPotion().isBadEffect())
                .collect(Collectors.toList());

        if (!beneficial.isEmpty()) {
            target.removePotionEffect(beneficial.get(entity.getRNG().nextInt(beneficial.size())).getPotion());
        }
    }
}