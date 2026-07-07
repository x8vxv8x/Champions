package c4.champions.common.affix.affix;

import c4.champions.common.affix.Affix;
import c4.champions.common.affix.AffixCategory;
import c4.champions.common.affix.AffixState;
import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class AffixInfested extends Affix {

    public AffixInfested() {
        super("infested", AffixCategory.OFFENSE);
    }

    @Override
    public AffixState createState() {
        return new ParasiteState();
    }

    @Override
    public void onInitialSpawn(EntityLiving entity, Champion cap) {
        ParasiteState buffer = cap.getState(this);
        buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, Math.max(1, (int)(entity.getMaxHealth()
                * ConfigHandler.affix.infested.silverfishPerHealth)));
        cap.markDirty(this);
    }

    @Override
    public void onSpawn(EntityLiving entity, Champion cap) {
        entity.tasks.addTask(0, new AISpawnParasite(entity));
    }

    @Override
    public float onHealed(EntityLiving entity, Champion cap, float amount, float newAmount) {

        if (newAmount > 0 && rand.nextFloat() < 0.5F) {
            ParasiteState buffer = cap.getState(this);
            buffer.num = Math.min(ConfigHandler.affix.infested.silverfishTotal, buffer.num + 2);
            cap.markDirty(this);
            return 0;
        }
        return newAmount;
    }

    @Override
    public void onDeath(EntityLiving entity, Champion cap, DamageSource source, LivingDeathEvent evt) {

        if (!entity.world.isRemote) {
            ParasiteState buffer = cap.getState(this);
            EntityLivingBase target = null;

            if (source.getTrueSource() instanceof EntityLivingBase) {
                target = (EntityLivingBase) source.getTrueSource();
            }
            boolean isEnder = entity instanceof EntityEnderman || entity instanceof EntityShulker || entity instanceof EntityEndermite || entity instanceof EntityDragon;

            List<EntityLiving> parasites = spawnParasites(entity.world, entity.getPosition(), buffer.num, isEnder);

            for (EntityLiving en : parasites) {
                en.setRevengeTarget(target);
            }
        }
    }

    private List<EntityLiving> spawnParasites(World world, BlockPos pos, int amount, boolean isEnder) {
        List<EntityLiving> parasites = Lists.newArrayList();

        for (int i = 0; i < amount; i++) {
            EntityLiving para = isEnder ? new EntityEndermite(world) : new EntitySilverfish(world);
            para.setLocationAndAngles((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            Champion chp = ChampionCapability.get(para);

            if (chp != null) {
                chp.setRank(RankManager.getEmptyRank());
            }
            world.spawnEntity(para);
            para.spawnExplosionParticle();
            parasites.add(para);
        }
        return parasites;
    }

    @Override
    public boolean canApply(EntityLiving entity) {
        return !(entity instanceof EntitySilverfish) && !(entity instanceof EntityEndermite);
    }

    class AISpawnParasite extends EntityAIBase {

        private final EntityLiving entity;
        private int attackTime;

        public AISpawnParasite(EntityLiving entityLiving) {
            this.entity = entityLiving;
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = entity.getAttackTarget();
            return isValidAffixTarget(entity, entitylivingbase, true) && entitylivingbase.world.getDifficulty()
                    != EnumDifficulty.PEACEFUL;
        }

        @Override
        public void startExecuting() {
            this.attackTime = ConfigHandler.affix.infested.silverfishInterval;
        }

        @Override
        public void updateTask() {

            if (entity.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.attackTime;
                EntityLivingBase entitylivingbase = entity.getAttackTarget();
                Champion chp = ChampionCapability.get(entity);

                if (entitylivingbase != null && chp != null) {
                    entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 180.0F, 180.0F);
                    ParasiteState buffer = chp.getState(AffixInfested.this);

                    if (this.attackTime <= 0 && buffer.num > 0) {
                        this.attackTime = ConfigHandler.affix.desecrator.attackInterval + entity.getRNG().nextInt(5) * 10;
                        boolean isEnder = entity instanceof EntityEnderman || entity instanceof EntityShulker || entity instanceof EntityEndermite || entity instanceof EntityDragon;

                        List<EntityLiving> parasites = spawnParasites(entity.world, entity.getPosition(),
                                ConfigHandler.affix.infested.silverfishAmount, isEnder);

                        for (EntityLiving en : parasites) {
                            en.setAttackTarget(entitylivingbase);
                        }
                        buffer.num = Math.max(0, buffer.num - parasites.size());
                        chp.markDirty(AffixInfested.this);
                    }
                }
                super.updateTask();
            }
        }
    }

    public static class ParasiteState implements AffixState {

        int num;

        @Override
        public void read(NBTTagCompound tag) {
            num = tag.getInteger("num");
        }

        @Override
        public void write(NBTTagCompound tag) {
            tag.setInteger("num", num);
        }
    }
}
