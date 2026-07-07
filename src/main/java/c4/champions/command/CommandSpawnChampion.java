package c4.champions.command;

import c4.champions.Champions;
import c4.champions.common.champion.ChampionService;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSpawnChampion extends CommandBase {

  public CommandSpawnChampion() {
  }

  @Override
  @Nonnull
  public String getName() {
    return "spawnchampion";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender sender) {
    return Champions.MODID + ".commands.spawnchampion.usage";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server,
                      @Nonnull ICommandSender sender, @Nonnull String[] args)
  throws CommandException {

    if (args.length < 2) {
      throw new WrongUsageException(getUsage(sender));
    }
    World world = sender.getEntityWorld();
    BlockPos pos = sender.getPosition();
    EntityLiving living = ChampionCommand.entity(world, args[0]);
    living.setPosition(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
    ChampionService.apply(living, ChampionCommand.rank(args[1]), ChampionCommand.affixes(args, 2));

    living.onInitialSpawn(world.getDifficultyForLocation(pos), null);
    world.spawnEntity(living);
    notifyCommandListener(sender, this,
                          Champions.MODID + ".commands.spawnchampion.success",
                          pos);
  }

  @Override
  @Nonnull
  public List<String> getTabCompletions(MinecraftServer server,
                                        ICommandSender sender, String[] args,
                                        @Nullable BlockPos targetPos) {
    return Collections.emptyList();
  }
}
