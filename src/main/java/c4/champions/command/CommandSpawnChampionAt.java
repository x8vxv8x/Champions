/*
 * Copyright (C) 2018-2019  C4
 *
 * This file is part of Champions, a mod made for Minecraft.
 *
 * Champions is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Champions is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Champions.  If not, see <https://www.gnu.org/licenses/>.
 */

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

public class CommandSpawnChampionAt extends CommandBase {

  public CommandSpawnChampionAt() {
  }

  @Override
  @Nonnull
  public String getName() {
    return "spawnchampionat";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2;
  }

  @Override
  @Nonnull
  public String getUsage(@Nonnull ICommandSender sender) {
    return Champions.MODID + ".commands.spawnchampionat.usage";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server,
                      @Nonnull ICommandSender sender, @Nonnull String[] args)
  throws CommandException {

    if (args.length < 5) {
      throw new WrongUsageException(getUsage(sender));
    }

    double posX = parseDouble(args[0]);
    double posY = parseDouble(args[1]);
    double posZ = parseDouble(args[2]);
    BlockPos blockPos = new BlockPos(posX, posY, posZ);

    World world = sender.getEntityWorld();
    EntityLiving living = ChampionCommand.entity(world, args[3]);
    living.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    ChampionService.apply(living, ChampionCommand.rank(args[4]), ChampionCommand.affixes(args, 5));

    living.onInitialSpawn(world.getDifficultyForLocation(blockPos), null);
    world.spawnEntity(living);
    notifyCommandListener(sender, this,
                          Champions.MODID + ".commands.spawnchampion.success",
                          blockPos);
  }

  @Override
  @Nonnull
  public List<String> getTabCompletions(MinecraftServer server,
                                        ICommandSender sender, String[] args,
                                        @Nullable BlockPos targetPos) {
    return Collections.emptyList();
  }
}
