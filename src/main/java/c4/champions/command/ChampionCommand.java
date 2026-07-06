package c4.champions.command;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

final class ChampionCommand {

    private ChampionCommand() {
    }

    static EntityLiving entity(World world, String id) throws CommandException {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(id), world);

        if (!(entity instanceof EntityLiving)) {
            throw new CommandException(Champions.MODID + ".commands.spawnchampion.entityError", id);
        }
        return (EntityLiving)entity;
    }

    static Rank rank(String value) throws CommandException {
        try {
            return RankManager.getRankForTier(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new CommandException(Champions.MODID + ".commands.spawnchampion.tierError", value);
        }
    }

    static int tier(String value) throws CommandException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new CommandException(Champions.MODID + ".commands.spawnchampion.tierError", value);
        }
    }

    static Set<String> affixes(String[] args, int start) throws CommandException {
        Set<String> affixes = Sets.newHashSet();

        for (int i = start; i < args.length; i++) {
            if (AffixRegistry.getAffix(args[i]) == null) {
                throw new CommandException(Champions.MODID + ".commands.spawnchampion.affixError", args[i]);
            }
            affixes.add(args[i]);
        }
        return affixes;
    }
}
