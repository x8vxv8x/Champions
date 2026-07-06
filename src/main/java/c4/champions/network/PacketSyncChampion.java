package c4.champions.network;

import c4.champions.common.champion.ChampionCapability;
import c4.champions.common.champion.Champion;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncChampion implements IMessage {

    private int entityId;
    private NBTTagCompound data;

    public PacketSyncChampion() {
    }

    public PacketSyncChampion(int entityId, Champion champion) {
        this.entityId = entityId;
        this.data = champion.write();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<PacketSyncChampion, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncChampion message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if (entity instanceof EntityLiving) {
                    Champion champion = ChampionCapability.get(entity);

                    if (champion != null) {
                        champion.read(message.data, false);
                    }
                }
            });
            return null;
        }
    }
}
