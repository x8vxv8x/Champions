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
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
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
    Entity entity =
            EntityList.createEntityByIDFromName(new ResourceLocation(args[0]),
                                                sender.getEntityWorld());

    if (!(entity instanceof EntityLiving)) {
      throw new CommandException(
              Champions.MODID + ".commands.spawnchampion.entityError", args[0]);
    }

    EntityLiving living = (EntityLiving) entity;

    int tier;

    try {
      tier = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      throw new CommandException(
              Champions.MODID + ".commands.spawnchampion.tierError", args[1]);
    }
    Set<String> argAffix = Sets.newHashSet();

    for (int i = 2; i < args.length; i++) {
      String affix = args[i];
      AffixBase affixBase = AffixRegistry.getAffix(affix);

      if (affixBase == null) {
        throw new CommandException(
                Champions.MODID + ".commands.spawnchampion.affixError",
                args[i]);
      }
      argAffix.add(args[i]);
    }

    World world = sender.getEntityWorld();
    BlockPos pos = sender.getPosition();
    living.setPosition(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
    ChampionHelper.applyChampionData(living, RankManager.getRankForTier(tier), argAffix);

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
