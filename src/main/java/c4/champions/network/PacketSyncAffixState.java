package c4.champions.network;

import c4.champions.common.affix.AffixInstance;
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

public class PacketSyncAffixState implements IMessage {

    private int entityId;
    private String identifier;
    private NBTTagCompound state;

    public PacketSyncAffixState() {
    }

    public PacketSyncAffixState(int entityId, AffixInstance instance) {
        this.entityId = entityId;
        this.identifier = instance.getIdentifier();
        this.state = instance.serializeState();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        identifier = ByteBufUtils.readUTF8String(buf);
        state = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, identifier);
        ByteBufUtils.writeTag(buf, state);
    }

    public static class Handler implements IMessageHandler<PacketSyncAffixState, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncAffixState message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);

                if (entity instanceof EntityLiving) {
                    Champion champion = ChampionCapability.get(entity);

                    if (champion != null) {
                        champion.updateAffixState(message.identifier, message.state);
                    }
                }
            });
            return null;
        }
    }
}
