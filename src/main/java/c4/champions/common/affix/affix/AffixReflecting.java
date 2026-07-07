package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AffixReflecting extends Affix {

  public AffixReflecting() {
    super("reflecting", AffixCategory.OFFENSE);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onDamagedEvent(LivingDamageEvent evt) {

    if (!ConfigHandler.affix.reflecting.killingBlow && evt.getSource().damageType
        .equals("reflecting")) {
      EntityLivingBase living = evt.getEntityLiving();
      float currentDamage = evt.getAmount();

      if (currentDamage >= living.getHealth()) {
        evt.setAmount(living.getHealth() - 1);
      }
    }
  }

  @Override
  public float onDamaged(EntityLiving entity, Champion cap, DamageSource source, float amount,
      float newAmount, LivingDamageEvent evt) {

    if (source.getTrueSource() instanceof EntityLivingBase) {
      EntityLivingBase entityLivingBase = (EntityLivingBase) source.getTrueSource();

      if (source.damageType.equals("reflecting") || (source instanceof EntityDamageSourceIndirect
          && ((EntityDamageSourceIndirect) source).getIsThornsDamage())) {
        return newAmount;
      }
      float min = (float) ConfigHandler.affix.reflecting.minimumPerc;
      source.damageType = "reflecting";

      if (source instanceof EntityDamageSource) {
        ((EntityDamageSource) source).setIsThornsDamage();
      }
      entityLivingBase.attackEntityFrom(source, (float) Math.min(
          amount * (entity.getRNG().nextFloat() * (ConfigHandler.affix.reflecting.maximumPerc - min)
              + min), ConfigHandler.affix.reflecting.maxDamage));
    }
    return newAmount;
  }
}
