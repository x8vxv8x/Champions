package c4.champions.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityArcticSpark extends AbstractEntitySpark {

    public EntityArcticSpark(World worldIn) {
        super(worldIn);
    }

    @SideOnly(Side.CLIENT)
    public EntityArcticSpark(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        super(worldIn, x, y, z, motionXIn, motionYIn, motionZIn);
    }

    public EntityArcticSpark(World worldIn, EntityLivingBase ownerIn, Entity targetIn, EnumFacing.Axis directionIn) {
        super(worldIn, ownerIn, targetIn, directionIn);
    }

    @Override
    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SNOWBALL;
    }

    @Override
    protected void bulletHit(RayTraceResult result) {
        if (result.entityHit == null) {
            ((WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 2, 0.2D, 0.2D, 0.2D, 0.0D);
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
        } else {
            if (result.entityHit instanceof EntityLivingBase) {
                EntityLivingBase hit = ((EntityLivingBase)result.entityHit);
                hit.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
                hit.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 2));
            }
        }
        this.setDead();
    }
}
