package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.affix.AffixState;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import javax.vecmath.Vector3d;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class AffixVortex extends Affix {

    public AffixVortex() {
        super("vortex", AffixCategory.CC);
    }

    @Override
    public AffixState createState() {
        return new VortexState();
    }

    @Override
    public void onUpdate(EntityLiving entity, Champion cap) {

        if (!entity.world.isRemote) {
            EntityLivingBase target = entity.getAttackTarget();

            if (isValidAffixTarget(entity, target, true)) {
                VortexState vortex = cap.getState(this);

                if (entity.ticksExisted % 40 == 0) {
                    float chance = vortex.mode ? 0.7f : 0.4f;

                    if (entity.getRNG().nextFloat() < chance) {
                        vortex.mode = !vortex.mode;
                        cap.markDirty(this);
                    }
                }

                if (vortex.mode) {
                    double x = entity.posX;
                    double y = entity.posY;
                    double z = entity.posZ;
                    double strength = ConfigHandler.affix.vortex.strength;
                    Vector3d vec = new Vector3d(x, y, z);
                    vec.sub(new Vector3d(target.posX, target.posY, target.posZ));
                    vec.normalize();
                    vec.scale(strength);
                    target.motionX += vec.x;
                    target.motionY += vec.y;
                    target.motionZ += vec.z;

                    if (target instanceof EntityPlayer) {
                        target.velocityChanged = true;
                    }
                }
            }
        }
    }

    public static class VortexState implements AffixState {

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
