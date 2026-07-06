package c4.champions.common.champion;

import c4.champions.common.affix.AffixInstance;
import c4.champions.network.NetworkHandler;
import c4.champions.network.PacketSyncAffixState;
import c4.champions.network.PacketSyncChampion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public final class ChampionSync {

    private ChampionSync() {
    }

    public static void full(EntityLiving living) {
        Champion champion = ChampionCapability.get(living);

        if (living.world instanceof WorldServer && champion != null && champion.getRank() != null) {
            PacketSyncChampion packet = new PacketSyncChampion(living.getEntityId(), champion);
            sendToTracking(living, packet);
        }
    }

    private static void affix(EntityLiving living, AffixInstance instance) {
        if (living.world instanceof WorldServer) {
            PacketSyncAffixState packet = new PacketSyncAffixState(living.getEntityId(), instance);
            sendToTracking(living, packet);
        }
    }

    public static void dirty(EntityLiving living) {
        Champion champion = ChampionCapability.get(living);

        if (champion == null) {
            return;
        }

        for (AffixInstance instance : champion.drainDirtyAffixes()) {
            affix(living, instance);
        }
    }

    public static void toPlayer(EntityLiving living, EntityPlayerMP player) {
        Champion champion = ChampionCapability.get(living);

        if (champion != null && champion.getRank() != null) {
            NetworkHandler.INSTANCE.sendTo(new PacketSyncChampion(living.getEntityId(), champion), player);
        }
    }

    private static void sendToTracking(EntityLiving living, net.minecraftforge.fml.common.network.simpleimpl.IMessage packet) {
        WorldServer world = (WorldServer)living.world;

        for (EntityPlayer player : world.getEntityTracker().getTrackingPlayers(living)) {
            if (player instanceof EntityPlayerMP) {
                NetworkHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)player);
            }
        }
    }
}
