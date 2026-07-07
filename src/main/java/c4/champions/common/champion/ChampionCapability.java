package c4.champions.common.champion;

import c4.champions.Champions;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.rank.RankManager;
import c4.champions.integrations.gamestages.ChampionStages;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ChampionCapability {

    @CapabilityInject(Champion.class)
    public static final Capability<Champion> CHAMPION_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Champions.MODID, "championship");

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static Champion get(final Entity entity) {

        if (entity instanceof EntityLiving && entity.hasCapability(CHAMPION_CAP, DEFAULT_FACING)) {
            return entity.getCapability(CHAMPION_CAP, DEFAULT_FACING);
        }
        return null;
    }

    @Nullable
    public static Champion getElite(final Entity entity) {
        Champion champion = get(entity);
        return champion != null && champion.isElite() ? champion : null;
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(Champion.class, new Capability.IStorage<Champion>() {
            @Override
            public NBTBase writeNBT(Capability<Champion> capability, Champion instance, EnumFacing side) {
                return instance.write();
            }

            @Override
            public void readNBT(Capability<Champion> capability, Champion instance, EnumFacing side, NBTBase nbt) {
                instance.read((NBTTagCompound)nbt);
            }
        }, Champion::new);
    }

    public static ICapabilityProvider createProvider(final Champion champion) {
        return new Provider(champion, CHAMPION_CAP, DEFAULT_FACING);
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<Champion> capability;
        final EnumFacing facing;
        final Champion instance;

        Provider (final Champion instance, final Capability<Champion> capability, @Nullable final EnumFacing
                facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<Champion> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final Champion getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability() != null ? getCapability().writeNBT(getInstance(), getFacing()) : new NBTTagCompound();
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {

            if (getCapability() != null) {
                getCapability().readNBT(getInstance(), getFacing(), nbt);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Champions.MODID)
    public static class EventHandler {

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            Entity entity = evt.getObject();

            if (ChampionRules.isChampionEntity(entity) && ChampionRules.isValidDimension(entity.dimension)) {
                evt.addCapability(ID, createProvider(new Champion()));
            }
        }

        @SubscribeEvent
        public static void entityJoin(EntityJoinWorldEvent evt) {
            Entity entity = evt.getEntity();

            if (!entity.world.isRemote && ChampionRules.isChampionEntity(entity)) {
                EntityLiving living = (EntityLiving)entity;
                Champion chp = get(living);

                if (chp != null && chp.getRank() == null) {
                    ChampionService.apply(living, ChampionService.generateRank(living));
                }
            }
        }

        @SubscribeEvent
        public static void entitySpawn(LivingSpawnEvent.SpecialSpawn evt) {
            Entity entity = evt.getEntity();

            if (!entity.world.isRemote && ChampionRules.isChampionEntity(entity)) {
                EntityLiving living = (EntityLiving)entity;
                Champion chp = get(living);

                if (chp != null) {

                    if (evt.getSpawner() != null && !ConfigHandler.championSpawners) {
                        chp.setRank(RankManager.getEmptyRank());
                        return;
                    }

                    if (Champions.isGameStagesLoaded && !ChampionStages.canSpawn(living)) {
                        chp.setRank(RankManager.getEmptyRank());
                        return;
                    }

                    if (chp.getRank() == null) {
                        ChampionService.apply(living, ChampionService.generateRank(living));
                    }
                }
            }
        }

        @SubscribeEvent
        public static void startTracking(PlayerEvent.StartTracking evt) {
            Entity entity = evt.getTarget();

            if (evt.getEntityPlayer() instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP)evt.getEntityPlayer();

                if (ChampionRules.isChampionEntity(entity)) {
                    Champion chp = get(entity);

                    if (chp != null && chp.isElite()) {
                        ChampionSync.toPlayer((EntityLiving)entity, playerMP);
                    }
                }
            }
        }
    }
}
