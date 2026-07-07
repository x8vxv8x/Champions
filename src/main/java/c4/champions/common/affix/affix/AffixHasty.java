package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AffixHasty extends Affix {

    public AffixHasty() {
        super("hasty", AffixCategory.OFFENSE);
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, Champion cap) {
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        speed.setBaseValue(speed.getBaseValue() + ConfigHandler.affix.hasty.movementBonus);
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null;
    }
}
