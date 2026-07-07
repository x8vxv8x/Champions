package c4.champions.command;

import c4.champions.Champions;
import c4.champions.common.init.ChampionsRegistry;
import c4.champions.common.item.ItemChampionPlacer;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;

public class CommandChampionEgg extends CommandBase {

    public CommandChampionEgg() {}

    @Override
    @Nonnull
    public String getName() {
        return "championegg";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return Champions.MODID + ".commands.championegg.usage";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
            throws CommandException {

        if (args.length < 2) {
            throw new WrongUsageException(getUsage(sender));
        }
        ChampionCommand.entity(sender.getEntityWorld(), args[0]);

        if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
            ItemStack stack = new ItemStack(ChampionsRegistry.championEgg);
            ItemChampionPlacer.applyEntityInfoToItemStack(stack, new ResourceLocation(args[0]),
                    ChampionCommand.tier(args[1]), ChampionCommand.affixes(args, 2));
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            notifyCommandListener(sender, this, Champions.MODID + ".commands.championegg.success", player.getName());
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
